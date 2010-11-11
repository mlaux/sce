package net.sce.bot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sce.bot.website.ScriptInfo;
import net.sce.bot.website.ScriptLoader;

public class ScriptSelector extends JDialog implements ActionListener, TreeSelectionListener {
	public static final int START_OPTION = 1;
	public static final int CANCEL_OPTION = 0;
	
	private static final String default_desc = "<html><i>Select a script to get information about it.</i></html>";
	private static final String default_author = "Author: <no script selected>";
	
	private JTree scriptTree;
	private JLabel authorLabel;
	private JEditorPane textPane;
	
	private int result;
	
	public ScriptSelector(Frame parent, ScriptLoader... loaders) {
		super(parent, "Script Selector");
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		scriptTree = new JTree(loadAllScripts(loaders));
		for (int k = 0; k < scriptTree.getRowCount(); k++)
			scriptTree.expandRow(k);
		scriptTree.setRootVisible(false);
		scriptTree.addTreeSelectionListener(this);
		scriptTree.setPreferredSize(new Dimension(150, 200));
		
		textPane = new JEditorPane("text/html", default_desc);
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(300, 200));
		
		authorLabel = new JLabel(default_author);
		
		JPanel editpanel = new JPanel(new BorderLayout(0, 5));
		editpanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		editpanel.add(authorLabel, BorderLayout.NORTH);
		editpanel.add(new JScrollPane(textPane), BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scriptTree, editpanel);
		pane.add(split, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton button = new JButton("Close");
		button.addActionListener(this);
		bottom.add(button);
		button = new JButton("Start script");
		button.addActionListener(this);
		bottom.add(button);
		pane.add(bottom, BorderLayout.SOUTH);
		
		add(pane);
		pack();
		setLocationRelativeTo(parent);
	}
	
	public int showDialog() {
		setVisible(true);
		return result;
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("Start script"))
			result = START_OPTION;
		else if(cmd.equals("Close"))
			result = CANCEL_OPTION;
		dispose();
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		Object obj = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();
		if(!(obj instanceof ScriptInfo)) {
			authorLabel.setText(default_author);
			textPane.setText(default_desc);
			return;
		}
		ScriptInfo info = (ScriptInfo) obj;
		authorLabel.setText("Author: " + info.author);
		textPane.setText(info.description);
	}
	
	private DefaultTreeModel loadAllScripts(ScriptLoader... loaders) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for(ScriptLoader load : loaders) {
			DefaultMutableTreeNode parent = new DefaultMutableTreeNode(load.getDescription());
			ScriptInfo[] scripts = load.loadScriptList();
			for(ScriptInfo script : scripts)
				parent.add(new DefaultMutableTreeNode(script));
			root.add(parent);
		}
		return new DefaultTreeModel(root);
	}
	
	public ScriptInfo getSelectedScript() {
		Object obj = ((DefaultMutableTreeNode) scriptTree.getSelectionPath()
				.getLastPathComponent()).getUserObject();
		if(!(obj instanceof ScriptInfo))
			return null;
		
		return (ScriptInfo) obj;
	}
}
