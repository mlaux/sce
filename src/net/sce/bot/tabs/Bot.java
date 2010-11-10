package net.sce.bot.tabs;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sce.bot.AccountManager;
import net.sce.bot.SCE;
import net.sce.bot.ScriptSelector;
import net.sce.bot.website.LocalScriptLoader;
import net.sce.bot.website.ScriptInfo;
import net.sce.debug.DebugSystem;
import net.sce.script.API;
import net.sce.script.Script;
import net.sce.util.FieldAccess;
import net.sce.util.ParamParser;

public class Bot extends SCETabbedPane.Tab implements AppletStub, Runnable {
	public static URL base_url, jar_url;
	private static final JPopupMenu botMenu;
	
	private Applet client, loader;
	private volatile boolean running = false;
	
	private AccountManager.Account account;
	private FieldAccess fieldAccess;
	
	private API api;
	private Script currentScript;
	
	public Bot(AccountManager.Account acc) {
		account = acc;
	}
	
	public void run() {
		try {
			Class<?> cl = new URLClassLoader(new URL[] { jar_url }).loadClass("loader");
			loader = (Applet) cl.newInstance();
			loader.setStub(this);
			add(loader);
			validate();
			loader.init();
			loader.start();
			for(Field fd : cl.getDeclaredFields()) {
				if(fd.getType().equals(Applet.class)) {
					fd.setAccessible(true);
					Applet app;
					do {
						app = (Applet) fd.get(loader);
						Thread.sleep(100);
					} while(app == null);
					client = app;
				}
			}
			initClientDependencies();
			running = true;
			while (running) {
				Thread.sleep(1000);
			}
			remove(loader);
			loader.stop();
			loader.destroy();
			loader = null;
			System.gc();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initClientDependencies() {
		try {
			// TODO Get hooks file based on client version
			fieldAccess = new FieldAccess(client.getClass().getClassLoader(), new File("hooks0.txt").toURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
		api = new API(this);
	}
	
	public void doClientPainting(Graphics g) {
		api.getInputManager().drawMouse(g);
		DebugSystem.draw(g, this);
		if(currentScript != null)
			currentScript.paint(g);
	}
	
	public void setScript(Script s) {
		currentScript = s;
	}
	
	// Should we restrict these?
	public Applet getClient() { return client; }
	public Canvas getCanvas() { return (Canvas) loader.getComponentAt(0, 0); }
	public Applet getLoader() { return loader; }
	public AccountManager.Account getAccount() { return account; }
	public API getAPI() { return api; }
	public FieldAccess getFieldAccess() { return fieldAccess; }
	
	// Boring implemented methods
	public void onClose() { running = false; }
	public String getParameter(String name) { return ParamParser.get(name); }
	public boolean isActive() { return true; }
	public URL getDocumentBase() { return base_url; }
	public URL getCodeBase() { return base_url; }
	public AppletContext getAppletContext() { return null; }
	public void appletResize(int width, int height) { }
	
	public static JPopupMenu getMenu() {
		return botMenu;
	}
	
	static {
		String base = "http://world1.runescape.com/";
		try {
			base_url = new URL(base);
			jar_url = new URL(base + "loader.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		botMenu = new JPopupMenu("Bot");
		BotMenuActionListener al = new BotMenuActionListener();
		JMenuItem it = new JMenuItem("Start script...");
		it.addActionListener(al);
		botMenu.add(it);
		botMenu.addSeparator();
		it = new JMenuItem("Stop script");
		it.addActionListener(al);
		botMenu.add(it);
	}
	
	private static final class BotMenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			Bot bot = SCE.getInstance().getCurrentBot();
			if(cmd.equals("Start script...")) {
				ScriptSelector ssel = new ScriptSelector(SCE.getInstance(), new LocalScriptLoader());
				ssel.setVisible(true);
				ScriptInfo info = ssel.getSelectedScript();
				if(info == null) return;
				Script script = info.load();
				bot.setScript(script);
				new Thread(script).start();
			} else if(cmd.equals("Stop script")) {
				// TODO Destroy script, stop threads, etc etc
				bot.setScript(null);
			}
		}
	}
}
