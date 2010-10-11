package net.sce.bot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class AccountSelectDialog extends JDialog implements ActionListener {
	private Component parent;
	private AccountManager.Account account;
	private JComboBox combo;
	private ButtonGroup buttons = new ButtonGroup();
	
	public AccountSelectDialog(Component parent) {
		super((Frame) null, "Select an account");
		this.parent = parent;
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		
		JPanel content = new JPanel(new BorderLayout(10, 0));
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.add(new JLabel(new ImageIcon("key.png")), BorderLayout.WEST);
		
		JPanel choice = new JPanel(new GridLayout(0, 1));
		choice.setBorder(new TitledBorder(""));
		JRadioButton bt = new JRadioButton("I want to use an existing account");
		bt.setActionCommand("existing");
		combo = new JComboBox(AccountManager.getAccounts());
		choice.add(bt);
		buttons.add(bt);
		buttons.setSelected(bt.getModel(), true);
		
		JPanel hack = new JPanel(new BorderLayout());
		hack.add(new JLabel("          "), BorderLayout.WEST);
		hack.add(combo, BorderLayout.CENTER);
		choice.add(hack);
		
		bt = new JRadioButton("I want to create a new account");
		bt.setActionCommand("new");
		choice.add(bt);
		buttons.add(bt);
		bt = new JRadioButton("I don't want to use an account for this bot");
		bt.setActionCommand("none");
		choice.add(bt);
		buttons.add(bt);
		
		content.add(choice, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton("OK");
		button.addActionListener(this);
		bottom.add(button);
		
		button = new JButton("Cancel");
		button.addActionListener(this);
		bottom.add(button);
		
		content.add(bottom, BorderLayout.SOUTH);
		
		add(content);
		pack();
		setLocationRelativeTo(parent);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("OK")) {
			String sel = buttons.getSelection().getActionCommand();
			if(sel.equals("existing")) {
				account = (AccountManager.Account) combo.getSelectedItem();
			} else if(sel.equals("new")) {
				AccountManager acm = new AccountManager(parent, AccountManager.Intent.MANAGE);
				acm.setVisible(true);
				account = acm.getInfo();
			} else if(sel.equals("none")) {
				account = AccountManager.null_account;
			}
		}
		dispose();
	}
	
	public AccountManager.Account getInfo() { return account; }
}
