package net.sce.bot.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class WelcomeTab extends SCETabbedPane.Tab implements Runnable, HyperlinkListener {
	static final String base_url = "http://strictfp.com/sce/";
	private JEditorPane editor;
	
	public WelcomeTab() {
		setLayout(new BorderLayout());
		setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));

		editor = new JEditorPane();
		editor.setEditable(false);
		editor.addHyperlinkListener(this);
		add(editor, BorderLayout.CENTER);
		new Thread(this).start();
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
	
	public void run() {
		try {
			editor.setPage(base_url + "welcome.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onClose() { }
}
