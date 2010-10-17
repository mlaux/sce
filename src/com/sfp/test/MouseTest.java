package com.sfp.test;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sce.script.input.Bezier;

/**
 * Used to eyeball mouse spline values so I could enter them into
 * my calculator and get a fitted exponential curve for finding 'k'
 * as a function of distance between 2 points.
 * Not really useful anymore since i've done that, but still cool
 * to look at.
 */
public class MouseTest extends Canvas implements MouseListener, MouseMotionListener {
	public double k = 0.15;
	
	private Point start = new Point(100, 100), end = new Point(200, 200);
	private Bezier spline;
	
	public MouseTest() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void paint(Graphics g) {
		if(spline == null) return;
		g.setColor(Color.black);
		for(Point pt : spline.getPath()) {
			g.fillRect(pt.x, pt.y, 3, 3);
		}
		g.setColor(Color.red);
		for(Point pt : spline.getControlPoints()) {
			g.fillRect(pt.x, pt.y, 5, 5);
		}
	}
	
	public void mousePressed(MouseEvent e) {
		start = new Point(e.getX(), e.getY());
	}
	
	public void mouseDragged(MouseEvent e) {
		end = new Point(e.getX(), e.getY());
		spline = new Bezier(start, end, k);
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Mouse test");
		JPanel top = new JPanel();
		final JTextField kField = new JTextField(6);
		JButton kUpdate = new JButton("Set k");
		
		top.add(kField);
		top.add(kUpdate);
		frame.add(top, BorderLayout.NORTH);
		
		final MouseTest mt = new MouseTest();
		kUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mt.k = Double.parseDouble(kField.getText());
			}
		});
		frame.add(mt, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}
