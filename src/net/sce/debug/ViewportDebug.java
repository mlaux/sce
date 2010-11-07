package net.sce.debug;

import java.awt.Color;
import java.awt.Graphics;

import net.sce.bot.tabs.Bot;
import net.sce.util.FieldAccess;

public class ViewportDebug extends PaintDebug {
	public String getName() {
		return "Viewport";
	}
	
	public Type getType() {
		return Type.TEXT;
	}
	
	public void draw(Graphics g, Bot bot) {
		FieldAccess fields = bot.getFieldAccess();
		
		g.setColor(Color.cyan);
		Object tk = fields.get("client.toolkit", null);
		if(tk == null) return;
		Object vp = fields.get("softwareToolkit.viewport", tk);
		if(vp == null) return;
		
		int y = 30;
		g.drawString("xmin: " + fields.getInt("softwareToolkit.minX", tk), 15, y += 15);
		g.drawString("xmax: " + fields.getInt("softwareToolkit.maxX", tk), 15, y += 15);
		g.drawString("ymin: " + fields.getInt("softwareToolkit.minY", tk), 15, y += 15);
		g.drawString("ymax: " + fields.getInt("softwareToolkit.maxY", tk), 15, y += 15);
		g.drawString("znear: " + fields.getInt("softwareToolkit.nearZ", tk), 15, y += 15);
		g.drawString("zfar: " + fields.getInt("softwareToolkit.farZ", tk), 15, y += 15);
		g.drawString("x1: " + fields.getFloat("viewport.x1", vp), 15, y += 15);
		g.drawString("x2: " + fields.getFloat("viewport.x2", vp), 15, y += 15);
		g.drawString("x3: " + fields.getFloat("viewport.x3", vp), 15, y += 15);
		g.drawString("y1: " + fields.getFloat("viewport.y1", vp), 15, y += 15);
		g.drawString("y2: " + fields.getFloat("viewport.y2", vp), 15, y += 15);
		g.drawString("y3: " + fields.getFloat("viewport.y3", vp), 15, y += 15);
		g.drawString("z1: " + fields.getFloat("viewport.z1", vp), 15, y += 15);
		g.drawString("z2: " + fields.getFloat("viewport.z2", vp), 15, y += 15);
		g.drawString("z3: " + fields.getFloat("viewport.z3", vp), 15, y += 15);
	}
}
