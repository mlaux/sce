package net.sce.script.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import net.sce.bot.tabs.Bot;

public class InputManager {
	private Component parent;
	private Component target;
	
	public static int realX = 0;
	public static int realY = 0;
	
	private int mouseX, mouseY;
	private int mouseSpeed = 2;
	
	private Point[] splinePoints;
	
	public InputManager(Bot bot) {
		parent = bot.getLoader();
		target = parent.getComponentAt(0, 0);
	}
	
	private void checkTargetValidity() {
		if(target != parent.getComponentAt(0, 0))
			target = parent.getComponentAt(0, 0);
	}
	
	public void teleportMouse(int x, int y) {
		checkTargetValidity();
		MouseEvent evt = new MouseEvent(target, MouseEvent.MOUSE_MOVED, 
				System.currentTimeMillis(), 0, x, y, 0, false);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(evt);
		mouseX = x;
		mouseY = y;
	}
	
	public void moveMouse(int x, int y) {
		Point a = new Point(mouseX, mouseY);
		Point b = new Point(x, y);
		Bezier bz = new Bezier(a, b);
		splinePoints = bz.getPath();
		
		for(int k = 0; k < splinePoints.length; k++) {
			Point pt = splinePoints[k];
				teleportMouse(pt.x, pt.y);
			try {
				Thread.sleep(50 - (mouseSpeed * 10));
			} catch (Exception e) { }
		}
		
		// splinePoints = null;
	}
	
	public void drawMouse(Graphics g) {
		g.setColor(Color.yellow);
		g.drawLine(mouseX - 8, mouseY, mouseX + 8, mouseY);
		g.drawLine(mouseX, mouseY - 8, mouseX, mouseY + 8);
		
		if(splinePoints != null && splinePoints.length > 2) {
			Point pt = splinePoints[0];
			g.setColor(Color.green);
			g.fillOval(pt.x - 3, pt.y - 3, 6, 6);
			
			g.setColor(Color.blue);
			for(int k = 1; k < splinePoints.length; k++) {
				Point a = splinePoints[k - 1];
				Point b = splinePoints[k];

				g.setColor(Color.blue);
				g.drawLine(a.x, a.y, b.x, b.y);
				g.setColor(Color.white);
				g.fillRect(b.x, b.y, 2, 2);
			}
			
			pt = splinePoints[splinePoints.length - 1];
			g.setColor(Color.red);
			g.fillOval(pt.x - 3, pt.y - 3, 6, 6);
		}
	}
}
