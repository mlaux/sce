package net.sce.script.types;

import net.sce.bot.tabs.Bot;
import net.sce.util.FieldAccess;

public class ClientMath {
	private static int[] sines = new int[16384];
	private static int[] cosines = new int[16384];
	
	private FieldAccess client;
	
	public ClientMath(Bot bot) {
		client = bot.getFieldAccess();
	}
	
	public XY tileToScreen(int x, int y) {
		return worldToScreen(
				((x - client.getInt("map.baseX", null)) << 9) + 256, 
				((y - client.getInt("map.baseY", null)) << 9) + 256, -1536);
	}
	
	public XY worldToScreen(int x, int y, int z) {
		int camx = client.getInt("camera.x", null);
		int camy = client.getInt("camera.y", null);
		int camz = client.getInt("camera.z", null);
		
		int pitch = client.getInt("camera.pitch", null);
		int yaw = client.getInt("camera.yaw", null);
		
		x -= camx;
		y -= camy;
		z -= camz;
		
		int sinx = sines[yaw];
		int siny = sines[pitch];
		
		int cosx = cosines[yaw];
		int cosy = cosines[pitch];
		
		int transx = (sinx * y) + (x * cosx)      >> 16;
		int transz = (y * cosx) - (sinx * x)      >> 15;
		int transy = (cosy * z) - (siny * transz) >> 16;
		    transz = (siny * z) + (transz * cosy) >> 16;

		if(transz == 0)
			return new XY(-1, -1);
		
		int sX = (transx << 9) / transz + 260;
		int sY = (transy << 9) / transz + 171;

		if(sX >= 4 && sX <= 516 && sY >= 4 && sY <= 340)
			return new XY(sX, sY);
		else
			return new XY(-1, -1);
	}
    
    static {
        double d = 0.00038349519697141029;
        for (int i = 0; i < 16384; i++) {
            sines[i] = (int) (32768 * Math.sin(i * d));
            cosines[i] = (int) (32768 * Math.cos(i * d));
        }
    }
    
    public static class XY {
    	public int x;
    	public int y;
    	
    	public XY(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
}
