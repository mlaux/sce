package net.sce.script.types;

import java.awt.Point;

import net.sce.bot.tabs.Bot;
import net.sce.util.FieldAccess;

public class MathUtils {
	private static int[] sines = new int[16384];
	private static int[] cosines = new int[16384];
	
	private FieldAccess client;
	
	public MathUtils(Bot bot) {
		client = bot.getFieldAccess();
	}
	
	public Point tileToScreen(int x, int y) {
		return worldToScreen(
				((x - client.getInt("client.baseX", null)) << 9) + 256, 
				((y - client.getInt("client.baseY", null)) << 9) + 256, -1536);
	}
	
	public Point worldToScreen(int x, int y, int z) {
		Object tk = client.get("client.toolkit", null);
		if(tk == null)
			return new Point(-1, -1);
		Object vp = client.get("softwareToolkit.viewport", tk);
		if(vp == null)
			return new Point(-1, -1);
		
		int minx = client.getInt("softwareToolkit.minX", tk);
		int maxx = client.getInt("softwareToolkit.maxX", tk);
		int miny = client.getInt("softwareToolkit.minY", tk);
		int maxy = client.getInt("softwareToolkit.maxY", tk);
		
		int nearz = client.getInt("softwareToolkit.nearZ", tk);
		int farz = client.getInt("softwareToolkit.farZ", tk);
		
		int scalex = client.getInt("softwareToolkit.scaleX", tk);
		int scaley = client.getInt("softwareToolkit.scaleY", tk);
		
		float offx = client.getFloat("viewport.offsetX", vp);
		float x1 = client.getFloat("viewport.x1", vp);
		float x2 = client.getFloat("viewport.x2", vp);
		float x3 = client.getFloat("viewport.x3", vp);
		
		float offy = client.getFloat("viewport.offsetY", vp);
		float y1 = client.getFloat("viewport.y1", vp);
		float y2 = client.getFloat("viewport.y2", vp);
		float y3 = client.getFloat("viewport.y3", vp);
		
		float offz = client.getFloat("viewport.offsetZ", vp);
		float z1 = client.getFloat("viewport.z1", vp);
		float z2 = client.getFloat("viewport.z2", vp);
		float z3 = client.getFloat("viewport.z3", vp);
		
		float transz = offz + (z1 * x + z2 * y + z3 * z);
		if(transz < nearz || transz > farz)
			return new Point(-1, -1);
		
		int transx = (int) ((scalex * (offx + (x1 * x + x2 * y + x3 * z))) / transz);
		int transy = (int) ((scaley * (offy + (y1 * x + y2 * y + y3 * z))) / transz);
		if(transx >= minx && transx <= maxx && transy >= miny && transy <= maxy)
			return new Point(transx - minx, transy - miny);
		return new Point(-1, -1);
	}
	
	public int getTileHeight(int x, int z) {
		byte[][][] htdata = (byte[][][]) client.get("client.heightData", null);
		Object[] planes = (Object[]) client.get("client.planes", null);
		if(htdata == null || planes == null) return 0;
		
		int plane = 0; // TODO current plane hook
		int x1 = x >> 9, z1 = z >> 9;
		
		if(x1 < 0 || x1 > 103 || z1 < 0 || z1 > 103)
			return 0;
		
		if(plane < 3 && (htdata[1][x1][z1] & 2) != 0) plane++;
		int[][] heights = (int[][]) client.get("plane.heights", planes[plane]);
		
		int x2 = x & 512 - 1;
		int y2 = z & 512 - 1;
		int begin = (heights[x1][z1]     * (512 - x2) + heights[x1 + 1][z1]     * x2) >> 9;
		int end =   (heights[x1][1 + z1] * (512 - x2) + heights[x1 + 1][z1 + 1] * x2) >> 9;
		return begin * (512 - y2) + end * y2 >> 9;
	}
    
    static {
        double d = 0.00038349519697141029;
        for (int i = 0; i < 16384; i++) {
            sines[i] = (int) (32768 * Math.sin(i * d));
            cosines[i] = (int) (32768 * Math.cos(i * d));
        }
    }
}
