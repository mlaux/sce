package net.sce.util;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.sce.bot.SCE;

public class SCEEventQueue extends EventQueue {
	
	protected void dispatchEvent(AWTEvent event) {
		Class<?> cl = event.getSource().getClass().getSuperclass();
		if(cl.equals(java.awt.Canvas.class)) {
			if(SCE.getInstance().isInputBlocked()
					&& isBlockedType(event)) return;
		}
		super.dispatchEvent(event);
	}
	
	private boolean isBlockedType(AWTEvent e) {
		return e instanceof MouseEvent || e instanceof KeyEvent || e instanceof FocusEvent;
	}
}
