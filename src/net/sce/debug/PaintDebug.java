package net.sce.debug;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import net.sce.bot.tabs.Bot;

public class PaintDebug extends JCheckBoxMenuItem {
	private DebugTask task;
	
	public PaintDebug(String text, Icon icon, DebugTask tsk) {
		super(text, icon);
		task = tsk;
	}
	
	public void draw(Graphics g, Bot bot) {
		if(isSelected())
			task.draw(g, bot);
	}
}
