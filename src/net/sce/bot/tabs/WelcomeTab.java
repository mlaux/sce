package net.sce.bot.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JEditorPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicHTML;

public class WelcomeTab extends SCETabbedPane.Tab implements HyperlinkListener {
	static final String base_url = "http://strictfp.com/sce/";
	static final String welcome = 
			"<center>" +
			"<img src=\"file:welcome.png\"><br>" +
			"<table cellspacing=\"10\"><tr>" +
			"<td><a href=\"func:newbot\"><img src=\"file:newbot.png\" border=\"0\"></a></td>" + 
			"<td><a href=\"func:accounts\"><img src=\"file:manageaccounts.png\" border=\"0\"></a></td>" + 
			"</tr><tr>" + 
			"<td><a href=\"page:store.html\"><img src=\"file:scriptstore.png\" border=\"0\"></a></td>" +
			"<td><a href=\"func:opensite\"><img src=\"file:visitwebsite.png\" border=\"0\"></a></td>" +
			"</tr></table></center>";
	
	private JEditorPane editor;
	
	public WelcomeTab() {
		setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));
		editor = new JEditorPane();
		try {
			editor.putClientProperty(BasicHTML.documentBaseKey, new File(System.getProperty("user.dir")).toURI().toURL());
			editor.setContentType("text/html");
			editor.setText(welcome);
		} catch (Exception e) {
			e.printStackTrace();
		}

		editor.setEditable(false);
		editor.addHyperlinkListener(this);
		add(editor, BorderLayout.CENTER);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String[] parts = e.getDescription().split(":");
			String cmd = parts[0];
			if(cmd.equals("func")) {
				fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, parts[1]));
			} else if(cmd.equals("page")) {
				try {
					editor.setPage(base_url + parts[1]);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void onClose() { }
}
