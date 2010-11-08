package net.sce.debug;

import net.sce.bot.tabs.Bot;
import net.sce.script.input.InputManager;

import java.awt.*;

public class MouseDebug extends PaintDebug {
	@Override
	public String getName() {
		return "Mouse";
	}

	@Override
	public Type getType() {
		return Type.PAINT;
	}

	@Override
	public void draw(Graphics g, Bot bot) {
		int x = InputManager.MOUSE_X;
		int y = InputManager.MOUSE_Y;
		g.drawString("(" + x + "," + y + ")", x + 10, y + 10);
	}
}
