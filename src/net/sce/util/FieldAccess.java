package net.sce.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldAccess {
	private Map<String, ClientField> fields;
	private ClassLoader classLoader;
	
	public FieldAccess(ClassLoader loader) {
		fields = new HashMap<String, ClientField>();
		classLoader = loader;
		// TODO Parse class/field info into the map from some source
		// File on disk or downloaded from the site
		// perhaps make constructor take source as parameter
	}
	
	public Object get(String key, Object on) {
		try {
			ClientField cf = fields.get(key);
			Class<?> cl = classLoader.loadClass(cf.className);
			Field f = cl.getDeclaredField(cf.fieldName);
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
	
	private class ClientField {
		private String className;
		private String fieldName;
		
		private ClientField(String c, String f) {
			className = c;
			fieldName = f;
		}
	}
}
