package net.sce.debug;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import net.sce.bot.tabs.Bot;

public class PaintDebug extends JCheckBoxMenuItem implements ActionListener {
	private DebugTask task;
	
	public PaintDebug(String text, Icon icon, DebugTask tsk) {
		super(text, icon);
		task = tsk;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		setSelected(!isSelected());
	}
	
	public void draw(Graphics g, Bot bot) {
		if(isSelected())
			task.draw(g, bot);
	}
}
