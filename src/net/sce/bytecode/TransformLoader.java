package net.sce.bytecode;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TransformLoader extends ClassLoader {
	private final String transformPackage;

	public TransformLoader(String pack) {
		transformPackage = pack;
	}

	/**
	 * Loads all the classes in a specified directory from either raw class
	 * files or from a jar archive
	 *
	 * @param dir Where to load the classes from
	 * @return The loaded classes
	 * @throws IOException
	 */
	public List<Class<?>> loadClasses(String base) throws IOException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		File dir = new File(base + transformPackage.replace('.', '/'));
		
		if (!dir.isDirectory())
			throw new IOException("Classes must be loaded from a directory");
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.getName().endsWith(".jar")) {
				classes.addAll(loadFromJar(f));
			}
			if (f.getName().endsWith(".class")) {
				classes.add(loadFromFile(f));
			}
		}
		return classes;
	}

	/**
	 * Loads a single class from a file
	 *
	 * @param f The file to load from
	 * @return The loaded class.
	 * @throws IOException
	 */
	public Class<?> loadFromFile(File f) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		byte[] bytes = new byte[dis.available()];
		dis.readFully(bytes);
		String name = f.getName();
		name = transformPackage + "." + name;
		name = name.replaceAll(".class", "");
		Class<?> c = defineClass(name, bytes, 0, bytes.length);
		return c;
	}

	/**
	 * Loads all the classes in a jar file.
	 *
	 * @param file The jar file to load from.
	 * @return An ArrayList of loaded classes.
	 * @throws IOException
	 */
	public List<Class<?>> loadFromJar(File file) throws IOException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.getName().endsWith(".class")) {
				String name = entry.getName().replaceAll(".class", "")
						.replaceAll("/", ".");
				DataInputStream dis = new DataInputStream(
						jarFile.getInputStream(entry));
				byte[] bytes = new byte[dis.available()];
				dis.readFully(bytes);
				Class<?> c = defineClass(name, bytes, 0, bytes.length);
				classes.add(c);
			}
		}
		return classes;
	}

	/**
	 * Sorts all the BytecodeTasks out of a list of classes.
	 *
	 * @param classes The classes to sort from
	 * @return The identified BytecodeTasks
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<BytecodeTask> sortBytecodeTasks(List<Class<?>> classes)
			throws IllegalAccessException, InstantiationException {
		List<BytecodeTask> tasks = new ArrayList<BytecodeTask>();
		Iterator<Class<?>> iterator = classes.iterator();
		while (iterator.hasNext()) {
			Class<?> c = iterator.next();
			if (BytecodeTask.class.isAssignableFrom(c)) {
				addDepends(tasks, c);
			}
		}
		return tasks;
	}
	
	private void addDepends(List<BytecodeTask> list, Class<?> cl) throws IllegalAccessException, InstantiationException {
		BytecodeTask task = (BytecodeTask) cl.newInstance();
		Dependent annotation = cl.getAnnotation(Dependent.class);
		if(annotation != null) {
			Class<?>[] depends = annotation.value();
			for(Class<?> dep : depends) {
				if(!checkAdd(list, dep)) {
					addDepends(list, dep);
				}
			}
		}
		if(!checkAdd(list, cl)) {
			System.out.println(" - " + cl.getSimpleName());
			list.add(task);
		}
	}
	
	private boolean checkAdd(List<BytecodeTask> list, Class<?> cl) {
		boolean listHas = false;
		for(BytecodeTask tsk : list) {
			if(tsk.getClass().equals(cl))
				listHas = true;
		}
		return listHas;
	}
}