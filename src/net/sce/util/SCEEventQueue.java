package net.sce.util;

import net.sce.bot.SCE;
import net.sce.script.input.InputManager;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SCEEventQueue extends EventQueue {
	
	protected void dispatchEvent(AWTEvent event) {
		Class<?> cl = event.getSource().getClass().getSuperclass();
		if(cl.equals(java.awt.Canvas.class)) {
			if(SCE.getInstance().isInputBlocked()
					&& isBlockedType(event)) return;
		}
		if(event.toString().contains("MOUSE_MOVED")) {
			MouseEvent me = (MouseEvent)event;
			InputManager.realX = me.getX();
			InputManager.realY = me.getY();
		}
		super.dispatchEvent(event);
	}
	
	private boolean isBlockedType(AWTEvent e) {
		return e instanceof MouseEvent || e instanceof KeyEvent || e instanceof FocusEvent;
	}
}
