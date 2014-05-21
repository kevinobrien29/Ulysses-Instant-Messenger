package com.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.common.Account;
import com.common.Contact;

public class AddContactWindow extends JFrame implements ActionListener {

	private JPanel panel;

	// Labels to identify the fields
	private JLabel nameLabel;
	private JButton oK;
	private JButton cancel;
	private Account account;
	private Messenger messenger;

	// Strings for the labels
	private String nameString = "Name: ";

	// Fields for data entry
	private JTextField nameField;

	public AddContactWindow(Account account, Messenger messenger) {
		panel = new JPanel();
		this.messenger = messenger;
		this.account = account;
		// Create the labels.
		nameLabel = new JLabel(nameString);
		// Create the text fields and set them up.
		nameField = new JTextField();
		nameField.setColumns(10);

		// Tell accessibility tools about label/textfield pairs.
		nameLabel.setLabelFor(nameField);

		oK = new JButton("OK");
		oK.setVerticalTextPosition(SwingConstants.BOTTOM);
		oK.setHorizontalTextPosition(SwingConstants.RIGHT);
		oK.setMnemonic(KeyEvent.VK_M);
		oK.setActionCommand("ok");
		oK.addActionListener(this);

		cancel = new JButton("Cancel");
		cancel.setVerticalTextPosition(SwingConstants.BOTTOM);
		cancel.setHorizontalTextPosition(SwingConstants.LEFT);
		cancel.setMnemonic(KeyEvent.VK_M);
		cancel.setActionCommand("cancel");
		cancel.addActionListener(this);

		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(nameLabel);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));
		fieldPane.add(nameField);

		JPanel buttonPane = new JPanel(new GridLayout(0, 2));
		buttonPane.add(oK);
		buttonPane.add(cancel);

		add(labelPane, BorderLayout.LINE_START);
		add(fieldPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public void actionPerformed(ActionEvent e) {
		if ("ok".equals(e.getActionCommand())) {
			Contact contact = new Contact(nameField.getText(), "", "", true);
			messenger.addFriend(contact);
		} else if ("cancel".equals(e.getActionCommand())) {
		}
	}

	public static void main(String[] args) {
		// Add contents to the window.
		AddContactWindow addContactWindow = new AddContactWindow(new Account(), new Messenger());
		addContactWindow.setVisible(true);
		addContactWindow.pack();
	}
}
