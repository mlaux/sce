package net.sce.debug;

import java.awt.Graphics;

import net.sce.bot.tabs.Bot;

public interface DebugTask {
	public void run(Bot b);
	
	public void draw(Graphics g, Bot b);
}
