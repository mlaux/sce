package net.sce.script;

import java.awt.Graphics;

import net.sce.bot.website.ScriptInfo;
import net.sce.script.input.InputManager;
import net.sce.script.types.MathUtils;

public abstract class Script implements Runnable {
	protected ScriptInfo info;
	
	protected MathUtils math;
	protected InputManager input;
	
	/* 
	 * Can't do this in a constructor because that would require
	 * every script to define a constructor which calls super(info, api)
	 * and that is sort of ugly.
	 */
	public void init(ScriptInfo i, API api) {
		info = i;
		
		math = api.getMathUtils();
		input = api.getInputManager();
	}
	
	/**
	 * Allows scripters to use either script() (Nexus-style scripting)
	 * or loop() (RSBot-style scripting). 
	 */
	public void run() {
		try {
			if(!onStart()) return;
			script();
			int time;
			while((time = loop()) > 0)
				Thread.sleep(time);
			onStop();
		} catch(Throwable e) {
			// Extremely broad exception handler for anything
			// that the scripter failed to catch.
			e.printStackTrace();
		}
	}
	
	public abstract boolean onStart();
	public abstract void onStop();
	public abstract void onPause();
	public abstract void onResume();
	public abstract void paint(Graphics g);

	public void script() { }
	public int loop() { return -1; }
}
