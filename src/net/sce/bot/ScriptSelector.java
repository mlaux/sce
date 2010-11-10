package net.sce.bot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

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

public class ScriptSelector extends JDialog implements TreeSelectionListener {
	private static final String default_desc = "<html><i>Select a script to get information about it.</i></html>";
	private static final String default_author = "Author: <no script selected>";
	
	private JLabel authorLabel;
	private JEditorPane textPane;
	
	public ScriptSelector(Frame parent, ScriptLoader... loaders) {
		super(parent, "Script Selector");
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JTree tree = new JTree(loadAllScripts(loaders));
		for (int k = 0; k < tree.getRowCount(); k++)
			tree.expandRow(k);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		tree.setPreferredSize(new Dimension(150, 200));
		
		textPane = new JEditorPane("text/html", default_desc);
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(300, 200));
		
		authorLabel = new JLabel(default_author);
		
		JPanel editpanel = new JPanel(new BorderLayout(0, 5));
		editpanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		editpanel.add(authorLabel, BorderLayout.NORTH);
		editpanel.add(new JScrollPane(textPane), BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, editpanel);
		pane.add(split, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton button = new JButton("Close");
		bottom.add(button);
		button = new JButton("Start script");
		bottom.add(button);
		pane.add(bottom, BorderLayout.SOUTH);
		
		add(pane);
		pack();
		setLocationRelativeTo(parent);
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
		return null;
	}
}
