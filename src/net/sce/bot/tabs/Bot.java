package net.sce.bot.tabs;

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

import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

public class Bot extends SCETabbedPane.Tab implements AppletStub {
	private static URL base_url, jar_url;
	private static final JPopupMenu botMenu;
	
	private Applet client, loader;
	
	private AccountManager.Account account;
	private FieldAccess fieldAccess;
	
	private API api;
	private Script currentScript;
	private ThreadGroup scriptThreads;
	
	public Bot(AccountManager.Account acc) {
		account = acc;
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onClose() {
		new Thread(new ShutdownTask()).start();
	}
	
	private void initClientDependencies() {
		try {
			// TODO Get hooks file based on client version
			fieldAccess = new FieldAccess(client.getClass().getClassLoader(), new File("hooks634.txt").toURI().toURL());
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
	
	@SuppressWarnings("deprecation")
	public void stopScript() {
		currentScript.onStop();
		scriptThreads.stop();
		currentScript = null;
		scriptThreads = null;
	}
	
	// Should we restrict these?
	public Applet getClient() { return client; }
	public Canvas getCanvas() { return (Canvas) loader.getComponentAt(0, 0); }
	public Applet getLoader() { return loader; }
	public AccountManager.Account getAccount() { return account; }
	public API getAPI() { return api; }
	public FieldAccess getFieldAccess() { return fieldAccess; }
	
	// Boring implemented methods
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
	
	private class ShutdownTask implements Runnable {
		public void run() {
			loader.stop();
			loader.destroy();
			loader = null;
			System.gc();
		}
	}
	
	private static class BotMenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			Bot bot = SCE.getInstance().getCurrentBot();
			if(cmd.equals("Start script...")) {
				if(bot == null) bot = SCE.getInstance().createBot();
				ScriptSelector ssel = new ScriptSelector(SCE.getInstance(), new LocalScriptLoader());
				if(ssel.showDialog() == ScriptSelector.START_OPTION) {
					ScriptInfo info = ssel.getSelectedScript();
					if(info == null) return;
					try {
						Script script = info.loadScript(bot);
						bot.currentScript = script;
						bot.scriptThreads = new ThreadGroup(script.getClass().getSimpleName());
						Thread th = new Thread(bot.scriptThreads, script, "Script");
						th.start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			} else if(cmd.equals("Stop script")) {
				bot.stopScript();
			}
		}
	}
}
