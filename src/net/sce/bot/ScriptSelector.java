package net.sce.bot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sce.bot.website.ScriptInfo;
import net.sce.bot.website.ScriptLoader;

public class ScriptSelector extends JDialog {
	public ScriptSelector(Frame parent, ScriptLoader... loaders) {
		super(parent, "Script Selector");
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JTree tree = new JTree(loadAllScripts(loaders));
		tree.setRootVisible(false);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, new JPanel());
		pane.add(split, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		pane.add(bottom, BorderLayout.SOUTH);
		
		add(pane);
		pack();
		setLocationRelativeTo(parent);
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
