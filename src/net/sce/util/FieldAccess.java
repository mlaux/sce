package net.sce.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		
		// Format is hook_name:class_name:fieldormethod_name:type:dummy
		// for example, client.cameraX:jd:Fc:I
		// method example: client.changeToolkit:aa:b:(IIII)V:23
		// would specify calling aa.b(23, x, x, x)
		// Empty lines and lines starting with # are ignored
		
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		while((line = br.readLine()) != null) {
			if(line.isEmpty() || line.startsWith("#")) continue;
			
			String[] parts = line.split(":");
			if(parts.length < 4) {
				System.out.println("malformed line in hooks file: " + line);
				continue;
			}
			
			fields.put(parts[0], new FieldOrMethod(parts[1], parts[2], parts[3], parts.length >= 5 ? parts[4] : "-1"));
		}
		br.close();
	}
	
	public Object get(String key, Object on) {
		FieldOrMethod cf = fields.get(key);
		return getDirect(cf.className, cf.compName, on);

	}

	public Object getDirect(String cName, String fName, Object on) {
		try {
			Class<?> cl = classLoader.loadClass(cName);
			Field f = cl.getDeclaredField(fName);
			f.setAccessible(true);
			return f.get(on);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void set(String key, Object on, Object value) {
		FieldOrMethod cf = fields.get(key);
		setDirect(cf.className, cf.compName, on, value);
	}
	
	public void setDirect(String cName, String fName, Object on, Object value) {
		try {
			Class<?> cl = classLoader.loadClass(cName);
			Field f = cl.getDeclaredField(fName);
			f.setAccessible(true);
			f.set(on, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
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
			Method mth = cl.getDeclaredMethod(fi.compName, getArgumentTypes(fi.compType));
			mth.setAccessible(true);
			if(fi.dummy != -1)
				return mth.invoke(on, fi.dummy);
			else
				return mth.invoke(on, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Class<?>[] getArgumentTypes(String signature) throws ClassNotFoundException {
		String types = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')'));
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for(int k = 0; k < types.length(); k++) {
			switch(types.charAt(k)) {
				case 'B': ret.add(Byte.TYPE); break;
				case 'S': ret.add(Short.TYPE); break;
				case 'I': ret.add(Integer.TYPE); break;
				case 'C': ret.add(Character.TYPE); break;
				case 'Z': ret.add(Boolean.TYPE); break;
				case 'J': ret.add(Long.TYPE); break;
				case 'F': ret.add(Float.TYPE); break;
				case 'D': ret.add(Double.TYPE); break;
				case 'L':
					String type = types.substring(k + 1, types.indexOf(';', k));
					type = type.replace("/", ".");
					ret.add(Class.forName(type));
					k += type.length();
					break;
			}
		}
		return ret.toArray(new Class<?>[ret.size()]);
	}
	
	/**
	 * Storage class for class/field names
	 */
	private static class FieldOrMethod {
		private String className;
		private String compName;
		private String compType;
		private int dummy;
		
		private FieldOrMethod(String c, String f, String t, String d) {
			className = c;
			compName = f;
			compType = t;
			dummy = Integer.parseInt(d);
		}
	}
}
