package net.sce.bot.tabs;

import java.applet.Applet;

import net.sce.bot.AccountManager;

public class Bot extends SCETabbedPane.Tab {
	private Applet client;
	private AccountManager.Account account;
	
	public Bot(AccountManager.Account acc) {
		account = acc;
		// blah blah load client
	}
	
	public void onClose() {
		if(client != null) {
			client.stop();
			client.destroy();
			client = null;
		}
		System.gc();
	}
}
