package net.sce.script;

import java.util.Map;

public abstract class Script extends WrapperProvider implements Runnable {
	private Map<String, String> arguments;
	
	public Script(API api, Map<String, String> args) {
		super(api);
		arguments = args;
	}
	
	/**
	 * Allows scripters to use either script() (Nexus-style scripting)
	 * or loop() (RSBot-style scripting). 
	 */
	public void run() {
		try {
			onStart();
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
	
	public abstract void onStart();
	public abstract void onStop();
	public abstract void onPause();
	public abstract void onResume();

	public void script() { }
	public int loop() { return -1; }
	
	public Map<String, String> getArguments() { return arguments; }
}
