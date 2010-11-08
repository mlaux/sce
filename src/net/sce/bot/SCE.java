package net.sce.bot;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import net.sce.bot.tabs.Bot;
import net.sce.bot.tabs.SCETabbedPane;
import net.sce.bot.tabs.WelcomeTab;
import net.sce.debug.DebugSystem;
import net.sce.util.SCEEventQueue;

public class SCE extends JFrame implements ActionListener {
	// uppercase constant names? what?
	public static final String app_name = "SCE";
	public static final String icon_base = "images/";
	public static final String website = "http://www.scebot.com/";
	
	private static SCE instance;
	private SCETabbedPane stp;
	private JToggleButton inputButton;
	
	private SCE() {
		super(app_name);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(icon_base + "icon.png").getImage());
		
		stp = new SCETabbedPane();
		WelcomeTab wt = new WelcomeTab();
		wt.addActionListener(this);
		stp.addTab("Welcome", wt);
		
		add(stp);
		addTopButtons();
		pack();
		setVisible(true);
		new MemoryTracker().start();
	}
	
	public void addTopButtons() {
		Container glassPane = (Container) getRootPane().getGlassPane();
		glassPane.setVisible(true);
		glassPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;

		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		inputButton = new JToggleButton("Human input");
		inputButton.setFocusable(false);
		inputButton.setIcon(new ImageIcon(icon_base + "yes.png"));
		inputButton.setSelectedIcon(new ImageIcon(icon_base + "no.png"));
		inputButton.setActionCommand("input");
		inputButton.addActionListener(this);
		
		JButton menu = new JButton("Bot");
		menu.setFocusable(false);
		menu.setIcon(new ImageIcon(icon_base + "bulldozer.png"));
		menu.setActionCommand("bot");
		menu.addActionListener(this);
		buttons.add(menu);
		
		menu = new JButton("Debug");
		menu.setFocusable(false);
		menu.setIcon(new ImageIcon(icon_base + "debug.png"));
		menu.setActionCommand("debug");
		menu.addActionListener(this);
		buttons.add(menu);
		
		buttons.add(inputButton);
		glassPane.add(buttons, gbc);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("newbot")) stp.createBot();
		else if(cmd.equals("accounts")) {
			AccountManager acm = new AccountManager(this, AccountManager.Intent.MANAGE);
			acm.setVisible(true);
		} else if(cmd.equals("opensite")) {
			try {
				Desktop.getDesktop().browse(new URI(website));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else if(cmd.equals("bot")) {
			Component c = (Component) e.getSource();
			Bot.getMenu().show(c, 0, c.getHeight());
		} else if(cmd.equals("debug")) {
			Component c = (Component) e.getSource();
			DebugSystem.getMenu().show(c, 0, c.getHeight());
		}
	}
	
	public boolean isInputBlocked() {
		return inputButton.isSelected();
	}
	
	public Bot getCurrentBot() {
		Component comp = stp.getSelectedComponent();
		if(comp == null || !(comp instanceof Bot)) return null;
		return (Bot) comp;
	}
	
	public static SCE getInstance() {
		return instance;
	}
	
	public static void main(String[] args) throws Exception {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
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
