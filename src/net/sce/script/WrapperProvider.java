package net.sce.script;

import net.sce.script.types.GameObject;
import net.sce.script.types.NPC;
import net.sce.script.types.Player;

public class WrapperProvider {
	private API api;
	
	protected WrapperProvider(API a) { api = a; }
	
	protected Player getMyPlayer() { return null; }
	
	protected Player getPlayerByName(String name) { return null; }
	
	protected GameObject getObjectByID(int id) { return null; }
	
	protected NPC getNPCByID(int id) { return null; }
}
