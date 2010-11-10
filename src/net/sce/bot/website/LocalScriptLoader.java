package net.sce.bot.website;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class LocalScriptLoader implements ScriptLoader {
	private static final String[] search_dirs = {
		System.getProperty("user.dir") + File.separator + "scripts" + File.separator,
		System.getProperty("user.home") + File.separator + "SCE" + File.separator + "scripts" + File.separator
	};
	
	public String getDescription() {
		return "Local";
	}
	
	public ScriptInfo[] loadScriptList() {
		List<ScriptInfo> ret = new ArrayList<ScriptInfo>();
		for(String dirname : search_dirs) {
			try {
				File[] files = new File(dirname).listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".class") && !name.contains("$");
					}
				});
				if(files == null) continue;
				URL pathURL = new File(dirname).toURI().toURL();
				URLClassLoader ucl = new URLClassLoader(new URL[] { pathURL });
				for(File file : files) {
					String name = file.getName().substring(0, file.getName().indexOf('.'));
					Class<?> cl = ucl.loadClass(name);
					String author = "Unknown", desc = "No description available";
					try {
						Field fld = cl.getDeclaredField("author");
						fld.setAccessible(true);
						author = (String) fld.get(null);
						fld = cl.getDeclaredField("description");
						fld.setAccessible(true);
						desc = (String) fld.get(null);
					} catch(Exception e) { }
					ret.add(new ScriptInfo(name, author, desc, pathURL));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ret.toArray(new ScriptInfo[0]);
	}
}
