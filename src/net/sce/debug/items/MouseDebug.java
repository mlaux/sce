package net.sce.debug.items;

import net.sce.bot.tabs.Bot;
import net.sce.debug.DebugTask;
import net.sce.script.input.InputManager;

import java.awt.*;

public class MouseDebug implements DebugTask {
	public void draw(Graphics g, Bot bot) {
		int x = InputManager.realX;
		int y = InputManager.realY;
		if(x >= 700)
			g.drawString("(" + x + "," + y + ")", x - 50, y + 10);      
		else
			g.drawString("(" + x + "," + y + ")", x + 10, y + 10);
	}

	public void run(Bot b) {
		
	}
}
