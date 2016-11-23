package net.sce.updater;

import net.sce.bytecode.BytecodeTask;
import net.sce.bytecode.Hook;
import net.sce.bytecode.SCEClassNode;
import net.sce.bytecode.TransformLoader;
import net.sce.util.Downloader;
import org.objectweb.asm.ClassReader;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Updater {
	public static final int REVISION = 1;
	public static final Map<String, SCEClassNode> identifiedClasses = new HashMap<String, SCEClassNode>();
	
	private Map<String, SCEClassNode> classes = new HashMap<String, SCEClassNode>();
	private ArrayList<Hook> hooks = new ArrayList<Hook>();
	private int loadedClasses;

	public Updater(String jarUrl, String transformPath, 
			boolean forceDownload) throws Exception {
		System.out.println("SCE updater revision " + REVISION);
		long time = System.currentTimeMillis();
		
		VersionThread vth = new VersionThread();
		vth.start();
		
		System.out.println("Using jar file '" + jarUrl + "'");
		loadClasses(jarUrl, forceDownload);
		System.out.println();
		
		System.out.println("Loading transforms...");
		TransformLoader transformLoader = new TransformLoader("net.sce.updater.transforms");
		List<Class<?>> transforms = transformLoader.loadClasses(transformPath);
		
		List<BytecodeTask> tasks = transformLoader.sortBytecodeTasks(transforms);
		System.out.println("Processing transforms...");
		
		int fails = 0;
		for(BytecodeTask task : tasks) {
			task.setClasses(classes);
			List<SCEClassNode> classList = new ArrayList<SCEClassNode>();
			classList.addAll(classes.values());
			
	   		List<SCEClassNode> possibleClasses = new ArrayList<SCEClassNode>();
	   		task.getPossibleClasses(classList, possibleClasses);
			Iterator<SCEClassNode> classIterator = possibleClasses.iterator();
			while(classIterator.hasNext()) {
				SCEClassNode classNode = classIterator.next();
				List<Hook> al = new ArrayList<Hook>();
				if(!task.transform(classNode, al)) fails++;
				hooks.addAll(al);
			}
		}
		
		System.out.println();
		System.out.println("Waiting for client version...");
		vth.join();
		
		int ver = vth.getVersion();
		System.out.println("*** Client version is " + ver);
		System.out.println("*** Loaded " + loadedClasses + " classes.");
		saveConfig("hooks" + ver + ".txt");
		System.out.println("*** " + identifiedClasses.size() + " classes identified.");
		System.out.println("*** " + hooks.size() + " fields hooked.");
		System.out.println("*** " + (tasks.size() - fails) + " out of " + tasks.size() + " transforms completed successfully");
		System.out.println("*** Update took " + (System.currentTimeMillis() - time) + " ms.");
	}

	public void saveConfig(String file) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
		for (Hook hook : hooks)
			bw.write(hook.toString() + "\r\n");
		bw.flush();
		System.out.println("*** Saved configuration to " + file);
	}

	/* Credits rs-hacking.com for this next method */
	public void loadClasses(String url, boolean force) {
		try {
			JarFile jf = new JarFile(Downloader.downloadFile(url, url.substring(url.lastIndexOf("/") + 1), force));
			Enumeration<JarEntry> eje = jf.entries();
			while (eje.hasMoreElements()) {
				JarEntry entry = eje.nextElement();
				if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
					String name = entry.getName();
					name = name.substring(0, name.indexOf(".class"));

					ClassReader cr = new ClassReader(jf.getInputStream(entry));
					SCEClassNode cn = new SCEClassNode();
					cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
					classes.put(name, cn);
				}
			}
			loadedClasses = classes.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			if(!validArgs(args)) {
				System.out.println("usage: Updater [OPTION]... jar_url transform_path");
				System.out.println(" --force-download (-f): download the JAR even if it exists");
				return;
			}
			boolean forceDownload = false;
			int k;
			for(k = 0; args[k].startsWith("-"); k++) {
				if(args[k].equals("--force-download") || args[k].equals("-f"))
					forceDownload = true;
			}
			new Updater(args[k++], args[k++], forceDownload);
			if(k < args.length) {
				System.out.println("Warning: ignored extra arguments");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean validArgs(String[] args) {
		if(args.length < 2 || args.length > 3) return false;
		int numNonOptions = 0;
		for(String s : args) {
			if(!s.startsWith("-")) numNonOptions++;
		}
		if(numNonOptions < 2) return false;
		return true;
	}
	
	class VersionThread extends Thread {
		private int version;
		
		public void run() {
			try {
				for (int ver = 630;; ver++) {
					Socket s = new Socket("world106.runescape.com", 43594);
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					DataInputStream dis = new DataInputStream(s.getInputStream());
					dos.writeByte(15);
					dos.writeInt(ver);
					dos.flush();
					if (dis.read() == 0) {
						version = ver;
						break;
					}
					dis.close();
					dos.close();
					s.close();
				}
			} catch(Exception e) { e.printStackTrace(); }
		}
		
		public int getVersion() {
			return version;
		}
	}
}
