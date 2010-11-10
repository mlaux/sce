package net.sce.bot.website;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import net.sce.bot.tabs.Bot;
import net.sce.script.Script;

public class ScriptInfo {
	public String name;
	public String author;
	public String description;
	public Map<String, String> arguments;
	public URL pathURL;
	
	public ScriptInfo(String n, String a, String d, URL u) {
		name = n;
		author = a;
		description = d;
		arguments = new HashMap<String, String>();
		pathURL = u;
	}
	
	public String toString() {
		return name;
	}

	public Script loadScript(Bot bot) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		URLClassLoader ucl = new URLClassLoader(new URL[] { pathURL });
		Class<?> cl = ucl.loadClass(name);
		Script scr = (Script) cl.newInstance();
		scr.init(this, bot.getAPI());
		return scr;
	}
}
