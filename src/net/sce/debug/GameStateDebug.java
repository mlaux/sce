package net.sce.debug;

import net.sce.bot.tabs.Bot;
import net.sce.util.FieldAccess;

import java.awt.*;

public class GameStateDebug extends PaintDebug {
	@Override
	public Type getType() {
		return Type.PAINT;
	}

	@Override
	public void draw(Graphics g, Bot bot) {
		FieldAccess fieldAccess = bot.getFieldAccess();
		g.drawString("GameState " + fieldAccess.getInt("client.gameState", null), 15, 400);
	}
}
