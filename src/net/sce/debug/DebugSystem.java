package net.sce.debug;

import net.sce.bot.tabs.Bot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

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
	
	private static void addAll(PaintDebug.Type type, JPopupMenu menu, ActionListener al) {
		for(String name : debugs.keySet()) {
			if(debugs.get(name).getType() == type) {
				JCheckBoxMenuItem mi = new JCheckBoxMenuItem(name);
				mi.addActionListener(al);
				menu.add(mi);
			}
		}
	}
	
	static {
		debugs = new LinkedHashMap<String, PaintDebug>();
		debugs.put("Map BaseX/Y", new BaseXYDebug());
		debugs.put("Viewport", new ViewportDebug());
		debugs.put("NPCs", new NPCDebug());
		debugs.put("Mouse", new MouseDebug());
		
		JPopupMenu menu = new JPopupMenu("Debug");
		DebugSystem ds = new DebugSystem();
		addAll(PaintDebug.Type.TEXT, menu, ds);
		menu.addSeparator();
		addAll(PaintDebug.Type.PAINT, menu, ds);
		debugMenu = menu;
	}
}
