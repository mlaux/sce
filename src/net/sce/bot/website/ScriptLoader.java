package net.sce.bot.website;

import net.sce.script.Script;

public interface ScriptLoader {
	public String getDescription();
	public ScriptInfo[] loadScriptList();
	
	public Script loadScript(ScriptInfo info);
}
