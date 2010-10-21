package net.sce.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FieldAccess {
	private Map<String, FieldOrMethod> fields;
	private ClassLoader classLoader;
	
	public FieldAccess(ClassLoader cloader, URL url) throws IOException {
		fields = new HashMap<String, FieldOrMethod>();
		classLoader = cloader;
		
		// Parse class/field/method info into the map from some source
		// TODO Make this more flexible, right now it only accepts a specific
		// file format, perhaps add a HookReader interface or something and
		// make the constructor take an instance of that.
		
		// Format is hook_name:class_name:field_name
		// for example, cameraX:jd:Fc
		// Empty lines and lines starting with # are ignored
		
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		while((line = br.readLine()) != null) {
			if(line.isEmpty() || line.startsWith("#")) continue;
			
			String[] parts = line.split(":");
			if(parts.length < 3) {
				System.out.println("malformed line in hooks file: " + line);
				continue;
			}
			fields.put(parts[0], new FieldOrMethod(parts[1], parts[2]));
		}
		br.close();
	}
	
	public Object get(String key, Object on) {
		try {
			FieldOrMethod cf = fields.get(key);
			Class<?> cl = classLoader.loadClass(cf.className);
			Field f = cl.getDeclaredField(cf.compName);
			f.setAccessible(true);
			return f.get(on);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Next methods remove need for tedious casting when using get()
	public int getInt(String key, Object on) {
		return ((Integer) get(key, on)).intValue();
	}

	public long getLong(String key, Object on) {
		return ((Long) get(key, on)).longValue();
	}

	public boolean getBoolean(String key, Object on) {
		return ((Boolean) get(key, on)).booleanValue();
	}

	public byte getByte(String key, Object on) {
		return ((Byte) get(key, on)).byteValue();
	}

	public char getChar(String key, Object on) {
		return ((Character) get(key, on)).charValue();
	}

	public short getShort(String key, Object on) {
		return ((Short) get(key, on)).shortValue();
	}

	public float getFloat(String key, Object on) {
		return ((Float) get(key, on)).floatValue();
	}

	public double getDouble(String key, Object on) {
		return ((Double) get(key, on)).doubleValue();
	}
	
	public Object callMethod(String key, Object on, Object... args) {
		FieldOrMethod fi = fields.get(key);
		try {
			Class<?> cl = classLoader.loadClass(fi.className);
			Method mth = cl.getDeclaredMethod(fi.compName);
			mth.setAccessible(true);
			return mth.invoke(on, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Storage class for class/field names
	 */
	private class FieldOrMethod {
		private String className;
		private String compName;
		
		private FieldOrMethod(String c, String f) {
			className = c;
			compName = f;
		}
	}
}
