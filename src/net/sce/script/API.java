package net.sce.script;

import net.sce.bot.tabs.Bot;
import net.sce.script.types.MathUtils;

public class API {
	private final Bot bot;
	
	private final MathUtils math;
	
	public API(Bot bot) {
		this.bot = bot;
		
		this.math = new MathUtils(bot);
	}
	
	public MathUtils getMathUtils() {
		return math;
	}
}
