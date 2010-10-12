package net.sce.bot.tabs;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import net.sce.bot.AccountManager;
import net.sce.bot.ParamParser;

public class Bot extends SCETabbedPane.Tab implements AppletStub, Runnable {
	public static URL base_url, jar_url;
	
	private Applet client;
	private volatile boolean running = false;
	private AccountManager.Account account;
	
	public Bot(AccountManager.Account acc) {
		account = acc;
		setLayout(new BorderLayout());
	}
	
	public void run() {
		try {
			Class<?> cl = new URLClassLoader(new URL[] { jar_url }).loadClass("loader");
			Applet loader = (Applet) cl.newInstance();
			loader.setStub(this);
			add(loader);
			validate();
			loader.init();
			loader.start();
			client = findClient(cl, loader);
			running = true;
			while(running) Thread.sleep(1000);
			remove(loader);
			loader.stop();
			loader.destroy();
			loader = null;
			System.gc();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private Applet findClient(Class<?> loader, Object inst) throws Exception {
		for(Field fd : loader.getDeclaredFields())
			if(fd.getType().equals(Applet.class)) {
				fd.setAccessible(true);
				Applet app;
				while((app = (Applet) fd.get(inst)) == null) Thread.sleep(100);
				return app;
			}
		return null;
	}
	
	public void onClose() { running = false; }
	public String getParameter(String name) { return ParamParser.get(name); }
	public boolean isActive() { return true; }
	public URL getDocumentBase() { return base_url; }
	public URL getCodeBase() { return base_url; }
	public AppletContext getAppletContext() { return null; }
	public void appletResize(int width, int height) { }
	
	static {
		String base = "http://world1.runescape.com/";
		try {
			base_url = new URL(base);
			jar_url = new URL(base + "loader.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
