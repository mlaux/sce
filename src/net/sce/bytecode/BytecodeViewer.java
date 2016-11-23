package net.sce.bytecode;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class BytecodeViewer extends JFrame implements ActionListener {
	private JTextPane textPane;
	
	public BytecodeViewer() {
		super("Bytecode Viewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JMenuBar jmb = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem it = new JMenuItem("Open...");
		it.addActionListener(this);
		menu.add(it);
		it = new JMenuItem("Exit");
		it.addActionListener(this);
		menu.addSeparator();
		menu.add(it);
		jmb.add(menu);
		setJMenuBar(jmb);
		
		textPane = new JTextPane();
		textPane.setFont(new Font("Consolas", Font.PLAIN, 14));
		add(new JScrollPane(textPane));
		setSize(640, 480);
		setLocationRelativeTo(null);
	}
	
	private void appendText(String text) {
		textPane.setText(textPane.getText() + text);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("Open...")) {
			JFileChooser fc = new JFileChooser("c:\\users\\matt\\workspace\\sceupdater\\");
			int result = fc.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				new JavapThread(fc.getSelectedFile()).start();
			}
		} else if(cmd.equals("Exit")) {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		new BytecodeViewer().setVisible(true);
	}
	
	class JavapThread extends Thread {
		private File file;
		
		public JavapThread(File f) {
			file = f;
			setDaemon(true);
		}
		
		public void run() {
			try {
				String name = file.getName();
				name = name.substring(0, name.lastIndexOf('.'));
				String path = ".;" + file.getParent();
				String command = "javap -classpath \"" + path + "\" -verbose -private \"" + name + "\"";
				appendText("Running " + command + "\n");
				appendText("-------------------------\n");
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				StringBuffer total = new StringBuffer();
				while((line = br.readLine()) != null) total.append(line + "\n");
				appendText(total.toString());
				p.destroy();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
