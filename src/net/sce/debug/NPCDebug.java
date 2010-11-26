package net.sce.debug;

import net.sce.bot.tabs.Bot;
import net.sce.script.types.MathUtils;
import net.sce.util.FieldAccess;

import java.awt.*;

public class NPCDebug extends PaintDebug {
	private static final Font idFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

	public Type getType() {
		return Type.PAINT;
	}

	public void draw(Graphics g, Bot bot) {
		FieldAccess fields = bot.getFieldAccess();
		Object[] npcnodes = (Object[]) fields.get("client.localNpcs", null);
		if(npcnodes == null) return;
		for(Object npcnode : npcnodes) {
			if(npcnode == null) continue;
			Object npc = fields.get("npcNode.npc", npcnode);
			if(npc == null) continue;
			Object def = fields.get("npc.definition", npc);
			if(def == null) continue;
			int x = fields.getInt("entity.localX", npc);
			int y = fields.getInt("entity.localY", npc);
			MathUtils mu = bot.getAPI().getMathUtils();
			int z = mu.getTileHeight(x, y) - 256;
			Point p = mu.worldToScreen(x, z, y);
			
			g.setColor(Color.cyan);
			g.fillRect(p.x, p.y, 3, 3);
			String name = (String) fields.get("npcDef.name", def);
			if(name == null || name.equals("null")) continue;
			int id = fields.getInt("npcDef.id", def);
			String desc = name + " (" + id + ")";
			
			Font oldFont = g.getFont();
			g.setColor(Color.white);
			g.setFont(idFont);
			g.drawString(desc, p.x - g.getFontMetrics().stringWidth(desc) / 2, p.y - 5);
			g.setFont(oldFont);
		}
	}
}