package net.sce.script;

import net.sce.bot.tabs.Bot;
import net.sce.script.input.InputManager;
import net.sce.script.types.MathUtils;

public class API {
	private final InputManager input;
	private final MathUtils math;
	
	public API(Bot bot) {
		this.input = new InputManager(bot);
		this.math = new MathUtils(bot);
	}
	
	public MathUtils getMathUtils() {
		return math;
	}
	
	public InputManager getInputManager() {
		return input;
	}
}
