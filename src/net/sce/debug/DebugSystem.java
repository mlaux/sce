package net.sce.debug;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sce.bot.tabs.Bot;
import net.sce.debug.items.BaseXYDebug;
import net.sce.debug.items.ConsoleCommandDebug;
import net.sce.debug.items.GameStateDebug;
import net.sce.debug.items.MouseDebug;
import net.sce.debug.items.NPCDebug;
import net.sce.debug.items.ViewportDebug;

public class DebugSystem {
	private static final List<OneTimeDebug> debugs;
	private static final List<PaintDebug> paints;
	private static final JPopupMenu debugMenu;
	
	public static void draw(Graphics g, Bot bot) {
		for(PaintDebug pd : paints)
			pd.draw(g, bot);
	}
	
	public static JPopupMenu getMenu() {
		return debugMenu;
	}
	
	private static void add(String text, Icon icon, DebugTask tsk, boolean paint) {
		JMenuItem it = paint ? new PaintDebug(text, icon, tsk) : new OneTimeDebug(text, icon, tsk);
		if(paint)
			paints.add((PaintDebug) it);
		else
			debugs.add((OneTimeDebug) it);
		debugMenu.add(it);
	}
	
	static {
		debugMenu = new JPopupMenu("Debug");
		
		debugs = new ArrayList<OneTimeDebug>();
		add("Run console command...", null, new ConsoleCommandDebug(), false);
		
		debugMenu.addSeparator();
		
		paints = new ArrayList<PaintDebug>();
		add("Map BaseX/Y", null, new BaseXYDebug(), true);
		add("Viewport", null, new ViewportDebug(), true);
		add("Game state", null, new GameStateDebug(), true);
		
		debugMenu.addSeparator();
		
		add("Mouse", null, new MouseDebug(), true);
		add("NPCs", null, new NPCDebug(), true);
	}
}
