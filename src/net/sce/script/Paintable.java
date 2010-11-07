package net.sce.script;

import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public interface Paintable {
	public void paint(Graphics g);
	
	class PaintDebug {
		enum Type { TEXT, DRAWING; }
		
		public String name;
		public Type type;
		public Paintable paintable;
		public boolean isOn;
		
		public PaintDebug(String n, Type t, Paintable p) {
			name = n;
			paintable = p;
			type = t;
		}
		
		public void paint(Graphics g) {
			paintable.paint(g);
		}
		
		public JMenuItem getMenuItem(ActionListener al) {
			JMenuItem it = new JMenuItem(name);
			if(al != null)
				it.addActionListener(al);
			it.setActionCommand(name.toLowerCase());
			return it;
		}
		
		public void toggleOn() {
			isOn = !isOn;
		}
	}
}
