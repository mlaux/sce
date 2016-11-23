package net.sce.deob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import net.sce.bytecode.InstructionSearcher;
import net.sce.bytecode.SCEClassNode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Deobfuscates the RS2 client.
 *
 * @author wintergreen985
 */
public class Deobfuscator implements Opcodes {
	private Map<String, SCEClassNode> classes = new HashMap<String, SCEClassNode>();

	public Deobfuscator() throws Exception {
		loadClasses(new File("./runescape.jar"));
		SCEClassNode client = classes.get("client");
		FieldNode cf = findOPField(client);
		fixControlFlow();
		dumpJar(new File("./rsi.jar"));
	}
	
	public FieldNode findOPField(SCEClassNode cn) {
		for (FieldNode fn : cn.fields) {
			if ((fn.access & Opcodes.ACC_PUBLIC) != 0
					&& (fn.access & Opcodes.ACC_STATIC) != 0
					&& (fn.desc.equals("Z") || fn.desc.equals("I")))
				return fn;
		}
		return null;
	}
	
	public void fixControlFlow() {
		for(SCEClassNode cn : classes.values()) {
			for(MethodNode mn : cn.methods) {
				
			}
		}
	}
	
	public void loadClasses(File file) throws IOException {
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry entry = enumeration.nextElement();
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				String name = entry.getName();
				name = name.substring(0, name.indexOf(".class"));
				ClassReader cr = new ClassReader(jarFile.getInputStream(entry));
				SCEClassNode cn = new SCEClassNode();
				cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				classes.put(name, cn);
			}
		}
	}

	public void dumpJar(File file) throws IOException {
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(file));
		for (SCEClassNode cn : classes.values()) {
			ClassWriter cw = new ClassWriter(0);
			cn.accept(cw);
			ZipEntry ze = new ZipEntry(cn.name + ".class");
			jos.putNextEntry(ze);
			jos.write(cw.toByteArray());
			if(cn.name.equals("client")) {
				FileOutputStream fos = new FileOutputStream("client.class");
				fos.write(cw.toByteArray());
				fos.close();
			}
		}
		jos.flush();
		jos.close();
	}

	public static void main(String[] args) throws Exception {
		new Deobfuscator();
	}
}
