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
import java.awt.event.WindowListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import com.client.Messenger.GroupConversationClient;
import com.client.Messenger.GroupConversationServer;
import com.common.Contact;
import com.common.ContactsList;

public class GroupConversationWindow extends JFrame implements ActionListener, KeyListener, WindowListener{

	private ContactsList contactsList = null;
	private Messenger messenger = null;

	// Various GUI components and info
	private JFrame mainFrame = null;
	private JTextArea chatText = null;
	private JTextField chatLine = null;
	private JTextField ipField = null;
	private JTextField portField = null;
	private JRadioButton hostOption = null;
	private JRadioButton guestOption = null;
	private JButton send = null;
	private ContactsList selectedFriends;
	private static FriendsPanel friendsPanel;
	private UUID code;
	private int clientOrServer = 0;

	GroupConversationWindow(ContactsList contactsList, Messenger messenger) {
		for (int i = 0; i < contactsList.size(); i++)
		{
			if (!contactsList.get(i).isOnline())
			{
				contactsList.removeoContact(contactsList.get(i));
			}
		}
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		this.messenger = messenger;
		this.contactsList = contactsList;
		selectedFriends = new ContactsList();
		UUID code = messenger.addGroupConversationServer(contactsList,
				chatText);
		this.code = code;
		friendsPanel = new FriendsPanel(messenger);
		friendsPanel.setCode(code);
		messenger.setTextArea(code, chatText);
		clientOrServer = 1;
		launch();
	}

	GroupConversationWindow(Messenger messenger, UUID code, FriendsPanel friendsPanel) {
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		messenger.setTextArea(code, chatText);
		this.messenger = messenger;
		this.code = code;
		this.friendsPanel = friendsPanel;
		friendsPanel.setCode(code);
		selectedFriends = new ContactsList();
		launch();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			String text = chatLine.getText();
			try {
				messenger.send(code, text);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			chatText.append(text);
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
	
	public void launch() {

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				messenger.endConversation(code);
			}
		});

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
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);
		mainPane.add(friendsPanel, BorderLayout.EAST);

		// Set up the main frame
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.add(mainPane);
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
				messenger.send(code, text);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			chatText.append(text);
			chatLine.setText("");
			this.repaint();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if (clientOrServer == 1)
		{
			try {
				GroupConversationServer conversation = (GroupConversationServer)messenger.get(code);
				conversation.end();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			GroupConversationClient conversation;
			try {
				conversation = (GroupConversationClient)messenger.get(code);
				conversation.end();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class FriendsPanel extends JPanel implements ActionListener {
	ContactsList contactsList;
	ContactsList invitedContacts;
	Messenger messenger;
	FriendsPanelTableModel model;
	UUID code;

	private JComboBox users = new JComboBox();

	JTable invites = null;

	private JButton add = new JButton("Add items");

	public FriendsPanel(Messenger messenger) {
		this.messenger = messenger;
		contactsList = messenger.getAccount().getContactsList();
		invitedContacts = new ContactsList();
		model = new FriendsPanelTableModel();
		invites = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(invites);
		
		for (int i = 0; i < contactsList.size(); i++) {
			users.addItem(contactsList.get(i));
		}
		add.addActionListener(this);
		JPanel options = new JPanel();
		options.add(users, BorderLayout.NORTH);
		options.add(add, BorderLayout.BEFORE_FIRST_LINE);

		this.setLayout(new BorderLayout());

		this.add(options, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed");
		Contact contact = (Contact) users.getSelectedItem();
		invitedContacts.addContact(contact);
		if (!model.contains(contact.toString()))
		{
			try {
				messenger.addFriendToGroupConversation(code, contact);
				model.add(contact.toString());
				invitedContacts.addContact(contact);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			invites.updateUI();
		}
		System.out.println("/actionPerformed");
	}
	
	public void setCode(UUID code) {
		this.code = code;
	}
	
	public void replaceFriends(ContactsList contacts) {
		try {
			model.clear();
			((FriendsPanelTableModel) invites.getModel()).clear();
			invites.updateUI();
			for (int i = 0; i < contacts.size(); i++)
			{
				((FriendsPanelTableModel) invites.getModel()).add(contacts.get(i).toString());
			}
			invites.updateUI();
			this.updateUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addFriend(Contact contact) {
		try {
			messenger.addFriendToGroupConversation(code, contact);
			((FriendsPanelTableModel) invites.getModel()).add(contact
					.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class FriendsPanelTableModel extends AbstractTableModel {

	private String[] columnNames = { "Name" };
	private Object[][] data = new Object[0][0];

	public void update(Object[][] value) {
		data = value;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public void clear() {
		data = new Object[0][1];
	}

	public void add(String name) {
		Object[][] data2 = new Object[data.length + 1][1];
		for (int i = 0; i < data.length; i++) {
			data2[i][0] = data[i][0];
		}
		data2[data.length][0] = name;
		data = data2;
	}

	public Boolean contains(String name) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][0].equals(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}