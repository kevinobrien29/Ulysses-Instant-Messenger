package com.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import com.common.Account;
import com.common.Contact;

public class ConversationWindow extends JFrame implements ActionListener,
		KeyListener {

	private Contact contact = null;
	private Messenger messenger = null;

	// Various GUI components and info
	private Account account;
	private JFrame mainFrame = null;
	private JTextArea chatText = null;
	private JTextField chatLine = null;
	private JLabel statusBar = null;
	private JTextField ipField = null;
	private JTextField portField = null;
	private JRadioButton hostOption = null;
	private JRadioButton guestOption = null;
	private JButton send = null;
	private UUID code;

	ConversationWindow(Contact contact, final Messenger messenger,
			Account account) {
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		this.setTitle(contact.getScreenName());
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		this.messenger = messenger;
		this.contact = contact;
		this.account = account;
		final UUID code = messenger.addConversation(contact, chatText);
		this.code = code;
		launch();
	}

	ConversationWindow(final Messenger messenger, final UUID code,
			Account account) {
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		messenger.setTextArea(code, chatText);
		this.messenger = messenger;
		this.code = code;
		this.account = account;
		launch();
	}

	public void launch() {

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				messenger.endConversation(code);
			}
		});

		// Set up the status bar
		statusBar = new JLabel();

		// Set up the options pane
		JPanel optionsPane = initOptionsPane();

		// Set up the chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		JScrollPane chatTextPane = new JScrollPane(chatText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(true);
		chatLine.addKeyListener(this);
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// Set up the main frame
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(mainPane);
		this.setSize(this.getPreferredSize());
		this.setLocation(200, 200);
		this.pack();
		this.setVisible(true);

	}

	private JPanel initOptionsPane() {

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));

		// Connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		send = new JButton("send");
		send.setMnemonic(KeyEvent.VK_C);
		send.addActionListener(this);
		send.setActionCommand("send");
		send.setEnabled(true);
		buttonPane.add(send);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("send")) {
			String text = chatLine.getText();
			try {
				messenger.send(code, account.getScreenName() + ":" + text
						+ "\n");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			chatText.append(account.getScreenName() + ":" + text + "\n");
			chatLine.setText("");
			this.repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			String text = chatLine.getText();
			try {
				messenger.send(code, account.getScreenName() + ":" + text
						+ "\n");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			chatText.append(account.getScreenName() + ":" + text + "\n");
			chatLine.setText("");
			this.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}