package net.sce.debug.items;

import net.sce.bot.tabs.Bot;
import net.sce.debug.DebugTask;
import net.sce.util.FieldAccess;

import java.awt.*;

public class GameStateDebug implements DebugTask {
	public void draw(Graphics g, Bot bot) {
		FieldAccess fieldAccess = bot.getFieldAccess();
		g.drawString("GameState " + fieldAccess.getInt("client.gameState", null), 15, 400);
	}

	public void run(Bot b) {
		
	}
}
