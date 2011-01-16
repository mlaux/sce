package net.sce.debug.items;

import java.awt.Color;
import java.awt.Graphics;

import net.sce.bot.tabs.Bot;
import net.sce.debug.DebugTask;
import net.sce.util.FieldAccess;

public class BaseXYDebug implements DebugTask {
	public void draw(Graphics g, Bot bot) {
		FieldAccess fields = bot.getFieldAccess();
		int baseX = fields.getInt("client.baseX", null);
		int baseY = fields.getInt("client.baseY", null);
		
		g.setColor(Color.cyan);
		g.drawString("Map region base: [ " + baseX + ", " + baseY + " ]", 15, 15);
	}

	public void run(Bot b) {
		
	}
}
