package com.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.common.Account;

public class Join extends JPanel implements ActionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Join.class);

	private Messenger messenger;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JLabel FTPPasswordLabel;

	private static String userNameString = "username: ";
	private static String passwordString = "password: ";
	private static String FTPPassword = "ftp password: ";

	private JFormattedTextField userNameField;
	private JFormattedTextField passwordField;
	private JFormattedTextField FTPPasswordField;
	private JButton confirm;

	public Join() {
		super(new BorderLayout());
		messenger = new Messenger();

		// Create the labels.
		userNameLabel = new JLabel(userNameString);
		passwordLabel = new JLabel(passwordString);
		FTPPasswordLabel = new JLabel(FTPPassword);
		// Create the text fields and set them up.
		userNameField = new JFormattedTextField();
		userNameField.setColumns(10);
		userNameField.setText("Username");

		passwordField = new JFormattedTextField();
		passwordField.setColumns(10);
		passwordField.setText("Password");
		
		FTPPasswordField = new JFormattedTextField();
		FTPPasswordField.setColumns(10);
		FTPPasswordField.setText("FTP Password");

		// Tell accessibility tools about label/textfield pairs.
		userNameLabel.setLabelFor(userNameField);
		passwordLabel.setLabelFor(passwordField);

		confirm = new JButton("join");
		confirm.setVerticalTextPosition(AbstractButton.CENTER);
		confirm.setHorizontalTextPosition(AbstractButton.LEADING);

		confirm.setMnemonic(KeyEvent.VK_D);
		confirm.setActionCommand("confirm");
		confirm.addActionListener(this);

		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(userNameLabel);
		labelPane.add(passwordLabel);
		labelPane.add(FTPPasswordLabel);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));
		fieldPane.add(userNameField);
		fieldPane.add(passwordField);
		fieldPane.add(FTPPasswordField);

		JPanel buttonPane = new JPanel(new GridLayout(0, 1));
		buttonPane.add(confirm);

		// Put the panels in this panel, labels on left,
		// text fields on right.
		setBorder(BorderFactory.createEmptyBorder(100, 100, 50, 50));
		add(labelPane, BorderLayout.WEST);
		add(fieldPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		Account account = new Account(userNameField.getText(), passwordField.getText(), "", FTPPasswordField.getText());
		if (messenger.Join(account))
		{
			System.out.println("Success");
		}
		else
		{
			System.out.println("nope");
		}
	}

	private static void createAndShowGUI() {
		if (logger.isDebugEnabled()) {
			logger.debug("createAndShowGUI() - start");
		}

		// Create and set up the window.
		JFrame frame = new JFrame("Startup");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Add contents to the window.
		frame.add(new Join());

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		if (logger.isDebugEnabled()) {
			logger.debug("createAndShowGUI() - end");
		}
	}
}