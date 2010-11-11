package net.sce.bot.website;

public interface ScriptLoader {
	public String getDescription();
	public ScriptInfo[] loadScriptList();
}
