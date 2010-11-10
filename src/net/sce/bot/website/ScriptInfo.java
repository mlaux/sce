package net.sce.bot.website;

import java.util.HashMap;
import java.util.Map;

import net.sce.script.Script;

public class ScriptInfo {
	public String name;
	public String author;
	public String description;
	public Map<String, String> arguments;
	
	private ScriptLoader loader;
	
	public ScriptInfo(String n, String a, String d, ScriptLoader l) {
		name = n;
		author = a;
		description = d;
		arguments = new HashMap<String, String>();
		
		loader = l;
	}
	
	public String toString() {
		return name;
	}
	
	public Script load() {
		return loader.loadScript(this);
	}
}
