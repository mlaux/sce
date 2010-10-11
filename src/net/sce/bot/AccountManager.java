package net.sce.bot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AccountManager extends JDialog implements ActionListener, ListSelectionListener {
	public enum Intent { CREATE, MANAGE };
	static final String filename = "accounts.txt";
	static final Account null_account = new Account("<none>", null, null);
	
	private Intent intent;
	private Account account;
	
	private JList accountsList;
	private DefaultListModel accountsModel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField bankpinField;
	
	public AccountManager(Component parent, Intent intent) {
		super((Frame) null, "Account Manager");
		this.intent = intent;
		this.accountsModel = new DefaultListModel();
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		//setResizable(false);
		
		JPanel content = new JPanel(new BorderLayout(10, 0));
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		if(intent == Intent.MANAGE) {
			Account[] accs = getAccounts();
			for(Account acc : accs) if(acc != null_account) accountsModel.addElement(acc);
			
			JPanel west = new JPanel(new BorderLayout());
			west.setBorder(new CompoundBorder(new TitledBorder("Accounts"), new EmptyBorder(5, 5, 5, 5)));
			accountsList = new JList(accountsModel);
			accountsList.addListSelectionListener(this);
			JScrollPane sp = new JScrollPane(accountsList);
			sp.setPreferredSize(new Dimension(150, 100));
			west.add(sp, BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			JButton button = new JButton(new ImageIcon("plus.png"));
			button.setActionCommand("add");
			button.addActionListener(this);
			button.setPreferredSize(new Dimension(25, 25));
			buttons.add(button);
			button = new JButton(new ImageIcon("minus.png"));
			button.setActionCommand("remove");
			button.addActionListener(this);
			button.setPreferredSize(new Dimension(25, 25));
			buttons.add(button);
			
			west.add(buttons, BorderLayout.SOUTH);
			content.add(west, BorderLayout.CENTER);
		}
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBorder(new CompoundBorder(new TitledBorder("Account Info"), new EmptyBorder(5, 5, 5, 5)));
		
		usernameField = new JTextField(12);
		passwordField = new JPasswordField(12);
		bankpinField = new JTextField(4);
		
		JPanel grid = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.weightx = 0.0;
		grid.add(new JLabel("Username: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1.0;
		grid.add(usernameField, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		grid.add(new JLabel("Password: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1.0;
		grid.add(passwordField, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		grid.add(new JLabel("Bank PIN: "), gbc);
		gbc.gridx++;
		gbc.weightx = 1.0;
		grid.add(bankpinField, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		
		center.add(grid, BorderLayout.NORTH);
		
		JButton button = new JButton("Save");
		button.addActionListener(this);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(button);
		center.add(bottom, BorderLayout.SOUTH);
		
		content.add(center, intent == Intent.MANAGE ? BorderLayout.EAST : BorderLayout.CENTER);
		
		if(intent == Intent.MANAGE) {
			JPanel okpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			button = new JButton("Finish");
			button.addActionListener(this);
			okpanel.add(button);
			content.add(okpanel, BorderLayout.SOUTH);
		}
		
		add(content);
		pack();
		setLocationRelativeTo(parent);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting() == true) return;
		Account acc = (Account) accountsList.getSelectedValue();
		if(acc == null) return;
		usernameField.setText(acc.username);
		passwordField.setText(acc.password);
		bankpinField.setText(acc.bank_pin);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("add")) {
			accountsModel.addElement(new Account("<new account>", "passwd", "0000"));
		} else if(cmd.equals("remove")) {
			accountsModel.removeElement(accountsList.getSelectedValue());
		} else if(cmd.equals("Save")) {
			String user = usernameField.getText();
			String pass = new String(passwordField.getPassword());
			String pin = bankpinField.getText();
			if(user.length() == 0 || user.contains(":") || pass.length() == 0 || pass.contains(":")) {
				JOptionPane.showMessageDialog(this, "You must enter a valid username and password.");
				return;
			}
			if(intent == Intent.CREATE) {
				Account acc = new Account(user, pass, pin);
				addAccount(acc);
				account = acc;
				dispose();
			} else {
				Account acc = (Account) accountsList.getSelectedValue();
				if(acc == null) return;
				accountsModel.removeElement(acc);
				accountsModel.addElement(new Account(user, pass, pin));
			}
		} else if(cmd.equals("Finish")) {
			saveAccounts();
			dispose();
		}
	}
	
	private void saveAccounts() {
		try {
			FileWriter fw = new FileWriter(filename, false);
			fw.write("### SCE accounts file\r\n");
			fw.write("### Created by SCE\r\n\r\n");
			for(Object obj : accountsModel.toArray()) {
				Account acc = (Account) obj;
				fw.write(acc.username + ":" + acc.password + ":" + acc.bank_pin + "\r\n");
			}
			fw.flush();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Account[] getAccounts() {
		List<Account> accounts = new ArrayList<Account>();
		try {
			BufferedReader fr = new BufferedReader(new FileReader(filename));
			String line;
			while((line = fr.readLine()) != null) {
				if(line.isEmpty() || line.startsWith("#")) continue;
				String[] parts = line.split(":");
				if(parts.length < 3) {
					System.out.println("Malformed accounts line: " + line);
					continue;
				}
				accounts.add(new Account(parts[0], parts[1], parts[2]));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(accounts.isEmpty()) accounts.add(null_account);
		return accounts.toArray(new Account[0]);
	}
	
	public static void addAccount(Account acc) {
		try {
			boolean makeHeader = false;
			File file = new File(filename);
			if(!file.exists()) makeHeader = true;
			
			FileWriter fw = new FileWriter(filename, true);
			if(makeHeader) {
				fw.write("### SCE accounts file\r\n");
				fw.write("### Created by SCE\r\n\r\n");
			}
			
			fw.write(acc.username + ":" + acc.password + ":" + acc.bank_pin + "\r\n");
			fw.flush();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Account getInfo() { return account; }
	
	public static class Account {
		public String username;
		public String password;
		public String bank_pin;
		
		public Account(String u, String p, String b) {
			username = u;
			password = p;
			bank_pin = b;
		}
		
		public String toString() { return username; }
	}
}
