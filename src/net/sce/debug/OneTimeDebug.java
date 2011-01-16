package net.sce.debug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import net.sce.bot.SCE;

public class OneTimeDebug extends JMenuItem implements ActionListener {
	private DebugTask task;
	
	public OneTimeDebug(String text, Icon icon, DebugTask tsk) {
		super(text, icon);
		task = tsk;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		task.run(SCE.getInstance().getCurrentBot());
	}
}
