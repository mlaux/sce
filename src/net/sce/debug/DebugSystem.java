package net.sce.debug;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import net.sce.bot.tabs.Bot;

public class DebugSystem implements ActionListener {
	private static final Map<String, PaintDebug> debugs;
	private static final JPopupMenu debugMenu;
	
	public static void draw(Graphics g, Bot bot) {
		for(PaintDebug pd : debugs.values()) {
			if(pd.isEnabled())
				pd.draw(g, bot);
		}
	}
	
	public static JPopupMenu getMenu() {
		return debugMenu;
	}
	
	public static void setDebugEnabled(String name, boolean on) {
		PaintDebug pd = debugs.get(name);
		if(pd != null) pd.setEnabled(on);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		PaintDebug pd = debugs.get(cmd);
		if(pd != null) pd.setEnabled(!pd.isEnabled());
	}
	
	static {
		debugs = new LinkedHashMap<String, PaintDebug>();
		debugs.put("Viewport", new ViewportDebug());
		JPopupMenu menu = new JPopupMenu("Debug");
		DebugSystem ds = new DebugSystem();
		for(String name : debugs.keySet()) {
			JCheckBoxMenuItem mi = new JCheckBoxMenuItem(name);
			mi.addActionListener(ds);
			menu.add(mi);
		}
		debugMenu = menu;
	}
}
