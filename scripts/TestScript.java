import java.awt.Graphics;

import net.sce.script.Script;

public class TestScript extends Script {
	private static final String author = "Tekk";
	private static final String description = "<html><i>Hi, I am a test script.</i></html>";
	
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	public void onResume() {
		// TODO Auto-generated method stub
		
	}
	
	public int loop() {
		return 1000;
	}
	
	public void paint(Graphics g) {
		g.drawString("Hi i am a script", 30, 30);
	}
}
