package net.sce.debug;

import java.awt.Color;
import java.awt.Graphics;

import net.sce.bot.tabs.Bot;
import net.sce.util.FieldAccess;

public class BaseXYDebug extends PaintDebug {
	public Type getType() {
		return Type.TEXT;
	}

	public void draw(Graphics g, Bot bot) {
		FieldAccess fields = bot.getFieldAccess();
		int baseX = fields.getInt("client.baseX", null);
		int baseY = fields.getInt("client.baseY", null);
		
		g.setColor(Color.cyan);
		g.drawString("Map region base: [ " + baseX + ", " + baseY + " ]", 15, 15);
	}
}
