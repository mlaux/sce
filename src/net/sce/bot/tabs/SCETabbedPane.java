package net.sce.bot.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sce.bot.AccountManager;
import net.sce.bot.AccountSelectDialog;
import net.sce.bot.SCE;

public class SCETabbedPane extends JTabbedPane {
	private List<SCETabbedPane.Tab> tabs;
	
	public SCETabbedPane() {
		tabs = new ArrayList<SCETabbedPane.Tab>();
	}
	
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		tabs.add((SCETabbedPane.Tab) component);
	}
	
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		if(!(component instanceof SCETabbedPane.Tab))
			throw new IllegalArgumentException("SCETabbedPanes can only contain SCETabbedPane.Tabs");
		super.insertTab(title, icon, component, tip, index);
		setTabComponentAt(index, new CloseLabel(title, (SCETabbedPane.Tab) component));
	}
	
	public Bot createBot() {
		AccountSelectDialog accsel = new AccountSelectDialog(this);
		accsel.setVisible(true);
		AccountManager.Account acc = accsel.getInfo();
		if(acc == null) return null; // they pressed cancel
		Bot bot = new Bot(acc);
		addTab(acc.username, bot);
		setSelectedIndex(indexOfComponent(bot));
		return bot;
	}
	
	public abstract static class Tab extends JPanel {
		static final Dimension tab_size = new Dimension(765, 503);
		private List<ActionListener> listeners = new ArrayList<ActionListener>();
		
		public Tab() {
			super(new BorderLayout());
		}
		
		public Dimension getPreferredSize() {
			return tab_size;
		}
		
		protected void fireActionPerformed(ActionEvent e) {
			for(ActionListener al : listeners) al.actionPerformed(e);
		}
		
		public void addActionListener(ActionListener al) { listeners.add(al); }
		public abstract void onClose();
	}
	
	private class CloseLabel extends JPanel {
		public CloseLabel(String title, final Tab comp) {
			setOpaque(false);
			
			JLabel lab = new JLabel(title);
			lab.setPreferredSize(new Dimension(lab.getPreferredSize().width, 16));
			add(lab);
			
			if(!title.equals("Welcome")) {
				lab = new JLabel(new ImageIcon(SCE.icon_base + "close.png"));
				lab.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) { comp.onClose(); SCETabbedPane.this.remove(comp); }
				});
				add(lab);
			}
		}
	}
}
