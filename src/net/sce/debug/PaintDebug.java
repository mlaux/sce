package net.sce.debug;

import java.awt.Graphics;

import net.sce.bot.tabs.Bot;

public abstract class PaintDebug {
	private boolean enabled;
	
	public void setEnabled(boolean b) {
		enabled = b;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public abstract String getName();
	public abstract void draw(Graphics g, Bot bot);
}
