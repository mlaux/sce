package net.sce.bot.tabs;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import net.sce.bot.AccountManager;
import net.sce.script.Paintable;
import net.sce.script.input.InputManager;
import net.sce.util.FieldAccess;
import net.sce.util.ParamParser;

public class Bot extends SCETabbedPane.Tab implements AppletStub, Runnable {
	public static URL base_url, jar_url;
	
	private Applet client, loader;
	private volatile boolean running = false;
	
	private AccountManager.Account account;
	private FieldAccess fieldAccess;
	private InputManager inputManager;
	private List<Paintable> paintables;
	
	public Bot(AccountManager.Account acc) {
		account = acc;
		paintables = new ArrayList<Paintable>();
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
				// XXX test purposes, moves mouse around applet randomly, remove eventually
				// Point pt = new Point((int) (Math.random() * 765), (int) (Math.random() * 503));
				// inputManager.moveMouse(pt.x, pt.y);
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
		inputManager = new InputManager(loader);
		paintables.add(inputManager);
		paintables.add(new Paintable() {
			public void paint(Graphics g) {
				// Paints the camera variables to do some testing.
				// TODO System to turn paint debug on/off via a menu in the bot
				//      or with function keys. Needs to work independently per-bot
				g.setColor(Color.cyan);
				g.drawString("Camera X: " + fieldAccess.getInt("camera.x", null), 15, 30);
				g.drawString("Camera Y: " + fieldAccess.getInt("camera.y", null), 15, 45);
				g.drawString("Camera Z: " + fieldAccess.getInt("camera.z", null), 15, 60);
				g.drawString("Camera pitch: " + fieldAccess.getInt("camera.pitch", null), 15, 75);
				g.drawString("Camera yaw: " + fieldAccess.getInt("camera.yaw", null), 15, 90);
			}
		});
	}
	
	public void doClientPainting(Graphics g) {
		for(Paintable pt : paintables) pt.paint(g);
	}
	
	// Should we restrict these?
	public Applet getClient() { return client; }
	public Applet getLoader() { return loader; }
	public AccountManager.Account getAccount() { return account; }
	public FieldAccess getFieldAccess() { return fieldAccess; }
	
	// Boring implemented methods
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
