package net.sce.bot;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import net.sce.bot.tabs.SCETabbedPane;
import net.sce.bot.tabs.WelcomeTab;
import net.sce.util.SCEEventQueue;

public class SCE extends JFrame implements ActionListener {
	// uppercase constant names? what?
	public static final String app_name = "SCE";
	
	private static SCE instance;
	private SCETabbedPane stp;
	private JToggleButton inputButton;
	
	private SCE() {
		super(app_name);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(new ImageIcon("icon.gif").getImage());
		
		stp = new SCETabbedPane();
		WelcomeTab wt = new WelcomeTab();
		wt.addActionListener(this);
		stp.addTab("Welcome", wt);
		
		add(stp);
		addUserInputButton();
		pack();
		setVisible(true);
		new MemoryTracker().start();
	}
	
	public void addUserInputButton() {
		Container glassPane = (Container) getRootPane().getGlassPane();
		glassPane.setVisible(true);
		glassPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;

		inputButton = new JToggleButton("Human input");
		inputButton.setIcon(new ImageIcon("yes.png"));
		inputButton.setSelectedIcon(new ImageIcon("no.png"));
		inputButton.setActionCommand("input");
		inputButton.addActionListener(this);
		glassPane.add(inputButton, gbc);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("newbot")) stp.createBot();
		else if(cmd.equals("accounts")) {
			AccountManager acm = new AccountManager(this, AccountManager.Intent.MANAGE);
			acm.setVisible(true);
		}
	}
	
	public boolean isInputBlocked() {
		return inputButton.isSelected();
	}
	
	public static SCE getInstance() {
		return instance;
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		instance = new SCE();
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(new SCEEventQueue());
	}
	
	// Update title bar every 5 seconds based on mem. usage
	class MemoryTracker extends Thread {
		public MemoryTracker() { setDaemon(true); }
		public void run() {
			while(true) {
				Runtime r = Runtime.getRuntime();
				long heap = r.totalMemory() / 1000l;
				long used = heap - (r.freeMemory() / 1000l);
				setTitle(app_name + " (" + used + "k used / " + heap + "k total)");
				try { Thread.sleep(5000); } catch(Exception e) { }
			}
		}
	}
}
