package net.sce.bot.website;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import net.sce.script.Script;

public class LocalScriptLoader implements ScriptLoader {
	private static final String[] search_dirs = {
		System.getProperty("user.dir") + File.separator + "scripts",
		System.getProperty("user.home") + File.separator + "SCE" + File.separator + "scripts"
	};
	
	public String getDescription() {
		return "Local";
	}
	
	public ScriptInfo[] loadScriptList() {
		List<ScriptInfo> ret = new ArrayList<ScriptInfo>();
		for(String dirname : search_dirs) {
			File[] files = new File(dirname).listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".class") && !name.contains("$");
				}
			});
			// TODO finish etc
		}
		return new ScriptInfo[0];
	}
	
	public Script loadScript(ScriptInfo info) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
