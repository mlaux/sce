package net.sce.debug.items;

import java.awt.Graphics;

import net.sce.bot.tabs.Bot;
import net.sce.debug.DebugTask;

public class InterfaceExplorerDebug implements DebugTask {
	public void run(Bot b) {
		b.getFieldAccess().setDirect("qq", "j", null, 9001);
	}

	public void draw(Graphics g, Bot b) {
		
	}
}
