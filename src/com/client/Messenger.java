package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.common.Account;
import com.common.Contact;
import com.common.ContactsList;
import com.common.messages.ControlMessage.AddFriendRequestControlMessage;
import com.common.messages.ControlMessage.ControlMessage;
import com.common.messages.ControlMessage.JoinRequestControlMessage;
import com.common.messages.ControlMessage.JoinResponseControlMessage;
import com.common.messages.ControlMessage.SignInRequestControlMessage;
import com.common.messages.ControlMessage.SignInResponseControlMessage;
import com.common.messages.ControlMessage.SignOutNotification;
import com.common.messages.ControlMessage.SignOutRequestControlMessage;
import com.common.messages.ControlMessage.UpdateFriendsListControlMessage;
import com.common.messages.NotificationMessage.ResponseMessage;
import com.nitido.utils.toaster.Toaster;

public class Messenger {
	private Account account;
	private ConversationListener conversationListener;
	private GroupConversationListener groupConversationListener;
	private Socket connectionToServer = null;
	private ObjectOutputStream outServer = null;
	private ObjectInputStream inServer = null;
	private ArrayList<Conversation> list = new ArrayList<Conversation>();
	private ArrayList<GroupConversationServer> groupConversationsServer = new ArrayList<GroupConversationServer>();
	private ArrayList<GroupConversationClient> groupConversationsClient = new ArrayList<GroupConversationClient>();
	private FriendsList friendsList;
	private UpdateThreadReceiver updateThreadReceiver;
	private String ip = "136.206.18.84";
	private boolean loggedIn = false;

	public FriendsList getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(FriendsList friendsList) {
		this.friendsList = friendsList;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void startUpdaterThread() {
		updateThreadReceiver = new UpdateThreadReceiver();
		updateThreadReceiver.start();
	}

	public void endUpdaterThread() {
		updateThreadReceiver.end();
	}

	public UUID addConversation(Contact contact, JTextArea textArea) {
		Conversation conversation = new Conversation(contact, textArea);
		conversation.start();
		list.add(conversation);
		return conversation.getCode();
	}

	public UUID addConversation(Socket socket) {
		Conversation conversation = new Conversation(socket);
		conversation.start();
		list.add(conversation);
		return conversation.getCode();
	}

	public UUID addGroupConversationServer(ContactsList contactsList,
			JTextArea textArea) {
		GroupConversationServer groupConversation = new GroupConversationServer(
				contactsList, textArea);
		groupConversation.start();
		groupConversationsServer.add(groupConversation);
		return groupConversation.getCode();
	}

	public UUID addGroupConversationServer(ContactsList contactsList) {
		GroupConversationServer groupConversation = new GroupConversationServer(
				contactsList);
		groupConversation.start();
		groupConversationsServer.add(groupConversation);
		return groupConversation.getCode();
	}

	public UUID addGroupConversationClient(Socket socket, FriendsPanel participants) {
		GroupConversationClient groupConversation = new GroupConversationClient(
				socket, participants);
		groupConversation.start();
		groupConversationsClient.add(groupConversation);
		return groupConversation.getCode();
	}

	public void send(UUID code, String text) throws Exception {
		String returnString = null;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCode().equals(code)) {
				list.get(i).Send(text);
				return;
			}
		}
		for (int i = 0; i < groupConversationsServer.size(); i++) {
			if (groupConversationsServer.get(i).getCode().equals(code)) {
				groupConversationsServer.get(i).Send(text);
				return;
			}
		}
		for (int i = 0; i < groupConversationsClient.size(); i++) {
			if (groupConversationsClient.get(i).getCode().equals(code)) {
				groupConversationsClient.get(i).Send(text);
				return;
			}
		}
		throw new Exception();
	}
	
	public Object get(UUID code) throws Exception {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCode().equals(code)) {
				return list.get(i);
			}
		}
		for (int i = 0; i < groupConversationsServer.size(); i++) {
			if (groupConversationsServer.get(i).getCode().equals(code)) {
				return list.get(i);
			}
		}
		for (int i = 0; i < groupConversationsClient.size(); i++) {
			if (groupConversationsClient.get(i).getCode().equals(code)) {
				return list.get(i);
			}
		}
		throw new Exception();
	}

	public void endConversation(UUID code) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCode().equals(code)) {
				list.get(i).end();
				list.get(i).stop();
				list.remove(i);
			}
		}
		for (int i = 0; i < groupConversationsServer.size(); i++) {
			if (groupConversationsServer.get(i).getCode().equals(code)) {
				groupConversationsServer.get(i).end();
			}
		}
		for (int i = 0; i < groupConversationsClient.size(); i++) {
			if (groupConversationsClient.get(i).getCode().equals(code)) {
				groupConversationsClient.get(i).end();
			}
		}
	}

	public void setTextArea(UUID code, JTextArea textArea) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCode().equals(code)) {
				list.get(i).setTextArea(textArea);
			}
		}
		for (int i = 0; i < groupConversationsServer.size(); i++) {
			if (groupConversationsServer.get(i).getCode().equals(code)) {
				groupConversationsServer.get(i).setTextArea(textArea);
			}
		}
		for (int i = 0; i < groupConversationsClient.size(); i++) {
			if (groupConversationsClient.get(i).getCode().equals(code)) {
				groupConversationsClient.get(i).setTextArea(textArea);
			}
		}
	}

	public void addFriendToGroupConversation(UUID code, Contact contact)
			throws Exception {
		for (int i = 0; i < groupConversationsServer.size(); i++) {
			if (groupConversationsServer.get(i).getCode().equals(code)) {
				groupConversationsServer.get(i).addNewFriend(contact);
				return;
			}
			throw new Exception();
		}
	}

	public Account getAccount() {
		return account;
	}

	public boolean Join(Account account) {
		Boolean result = false;
		try {
			connectionToServer = new Socket(ip, 20);
			outServer = new ObjectOutputStream(connectionToServer
					.getOutputStream());
			inServer = new ObjectInputStream(connectionToServer
					.getInputStream());
			JoinRequestControlMessage output = new JoinRequestControlMessage(
					account);
			outServer.writeObject(output);
			JoinResponseControlMessage input = (JoinResponseControlMessage) inServer
					.readObject();
			if (input.getType() == 502 && input.isSuccessfull()) {
				result = true;
			}
			inServer.close();
			outServer.close();
			connectionToServer.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
		return result;
	}

	public boolean addFriend(Contact contact) {
		Boolean result = false;
		try {
			connectionToServer = new Socket(ip, 20);
			outServer = new ObjectOutputStream(connectionToServer
					.getOutputStream());
			inServer = new ObjectInputStream(connectionToServer
					.getInputStream());
			AddFriendRequestControlMessage output = new AddFriendRequestControlMessage(
					account, contact);
			outServer.writeObject(output);
			ResponseMessage input = (ResponseMessage) inServer.readObject();
			if (input.getType() == 702 && input.isSuccess()) {
				result = true;
				System.out.println("success");
			}
			inServer.close();
			outServer.close();
			connectionToServer.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
		return result;
	}

	public boolean deleteFriend(final Account account, Contact contact) {
		Boolean result = false;
		try {
			connectionToServer = new Socket(ip, 20);
			outServer = new ObjectOutputStream(connectionToServer
					.getOutputStream());
			inServer = new ObjectInputStream(connectionToServer
					.getInputStream());
			ControlMessage output = new ControlMessage(account, contact);
			output.setType(801);
			outServer.writeObject(output);
			ResponseMessage input = (ResponseMessage) inServer.readObject();
			System.out.println("success? " + input.getType()
					+ input.isSuccess());
			if (input.getType() == 802 && input.isSuccess()) {
				result = true;
				account.removeoContact(contact);

				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						friendsList.Load(account.getContactsList());
						friendsList.updateUI();
					}
				});
				System.out.println("success");
			}
			inServer.close();
			outServer.close();
			connectionToServer.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
		return result;
	}

	public boolean Login(String userName, String password) {
		Boolean result = false;
		try {
			connectionToServer = new Socket(ip, 20);
			outServer = new ObjectOutputStream(connectionToServer
					.getOutputStream());
			inServer = new ObjectInputStream(connectionToServer
					.getInputStream());
			InetAddress thisIp = InetAddress.getLocalHost();
			ControlMessage output = new SignInRequestControlMessage(
					new Account(userName, password, thisIp.getHostAddress()));
			outServer.writeObject(output);
			SignInResponseControlMessage input = (SignInResponseControlMessage) inServer
					.readObject();
			if (input.getType() == 102 && input.isSuccessfull()) {
				account = input.getAccount();
				account.setPassword(password);
				result = true;
				loggedIn = true;
			}
			inServer.close();
			outServer.close();
			connectionToServer.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
		return result;
	}

	public boolean Logout(String userName, String password) {
		Boolean result = false;
		try {
			connectionToServer = new Socket(ip, 20);
			outServer = new ObjectOutputStream(connectionToServer
					.getOutputStream());
			inServer = new ObjectInputStream(connectionToServer
					.getInputStream());
			InetAddress thisIp = InetAddress.getLocalHost();
			ControlMessage output = new SignOutRequestControlMessage(
					new Account(userName, password, thisIp.getHostAddress()));
			outServer.writeObject(output);
			inServer.close();
			outServer.close();
			connectionToServer.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
		return result;
	}

	public void startConversationListener(Account account) {
		conversationListener = new ConversationListener(this, account);
		conversationListener.start();
	}

	public void startGroupConversationListener() {
		groupConversationListener = new GroupConversationListener(this);
		groupConversationListener.start();
	}

	class UpdateThreadReceiver extends Thread {
		ServerSocket serverSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Boolean running = null;

		public void end() {
			running = false;
		}

		public void run() {
			running = true;
			try {
				serverSocket = new ServerSocket(50);

			} catch (IOException e) {
				System.err.println("Could not listen on port: 50");
				e.printStackTrace();
			}

			Socket socket = null;

			while (running) {
				try {

					socket = serverSocket.accept();

					out = new ObjectOutputStream(socket.getOutputStream());
					in = new ObjectInputStream(socket.getInputStream());

					ControlMessage input = (ControlMessage) in.readObject();
					ControlMessage message = input;
					System.out.println("Received something");
					int messageType = input.getType();

					if (messageType == 301) {
						UpdateFriendsListControlMessage messages = (UpdateFriendsListControlMessage) input;
						Contact contact = messages.getContact();
						System.out.println("received IP" + contact.getIP());
						account.updateContact(contact);

						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								friendsList.Load(account.getContactsList());
								friendsList.updateUI();
							}
						});

						Toaster toasterManager = new Toaster();
						toasterManager.showToaster(contact.getScreenName()
								+ " has logged in.");
					} else if (messageType == 501) {
						SignOutNotification messages = (SignOutNotification) input;
						Contact contact = messages.getContact();
						System.out.println("received IP" + contact.getIP());
						contact.setOnline(false);
						account.updateContact(contact);
						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								friendsList.Load(account.getContactsList());
								friendsList.updateUI();
							}
						});

						Toaster toasterManager = new Toaster();
						toasterManager.showToaster(contact.getScreenName()
								+ " has logged out.");
					} else if (messageType == 701) {
						Contact contact = message.getContact();
						account.addContact(contact);

						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								friendsList.Load(account.getContactsList());
								friendsList.updateUI();
							}
						});
					} else if (messageType == 901) {
						Contact contact = message.getContact();
						account.removeoContact(contact);

						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								friendsList.Load(account.getContactsList());
								friendsList.updateUI();
							}
						});
					}

					in.close();
					out.close();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	class Conversation extends Thread {
		public UUID code;
		public Socket echoSocket = null;
		public ObjectOutputStream out = null;
		public ObjectInputStream in = null;
		String fullText = "";
		JTextArea textArea;
		Document model;
		Contact contact;
		public Boolean running;
		public Boolean main;
		int position = 0;

		Conversation(Contact contact, JTextArea textArea) {
			try {
				this.contact = contact;
				echoSocket = new Socket(contact.getIP(), 30);
				out = new ObjectOutputStream(echoSocket.getOutputStream());
				in = new ObjectInputStream(echoSocket.getInputStream());
				code = code.randomUUID();
				this.textArea = textArea;
				this.model = this.textArea.getDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Conversation(Socket socket) {
			this.echoSocket = socket;
			try {
				this.out = new ObjectOutputStream(socket.getOutputStream());
				this.in = new ObjectInputStream(socket.getInputStream());
				code = code.randomUUID();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void setTextArea(JTextArea textArea) {
			this.textArea = textArea;
			this.model = this.textArea.getDocument();
		}

		public UUID getCode() {
			return code;
		}

		public void setCode(UUID code) {
			this.code = code;
		}

		public void Send(String text) {
			Message output = new Message(text, account.toContact());
			try {
				if (running != false) {
					out.writeObject(output);
					position = position + text.length();
					out.flush();
				} else {
					System.out.println("reconnecting");
					reconnect(text);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("reconnecting");
				reconnect(text);
			}

		}

		public void reconnect(String text) {
			try {
				echoSocket = new Socket(contact.getIP(), 30);
				out = new ObjectOutputStream(echoSocket.getOutputStream());
				in = new ObjectInputStream(echoSocket.getInputStream());
				main = true;
				Send(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void end() {
			try {
				echoSocket.close();
				out.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			running = false;
		}

		public void run() {
			Message input = null;
			running = true;
			main = true;
			while (running) {
				if (main) {
					try {
						input = (Message) in.readObject();
						contact = input.getContact();
						fullText = fullText + input.getText();
						model.insertString(position, input.getText(), null);
						position = position + input.getText().length();
						textArea.updateUI();
					} catch (Exception e) {
						e.printStackTrace();
						main = false;
					}
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	class GroupConversationClient extends Thread {
		public UUID code;
		public Socket socket = null;
		public ObjectOutputStream out = null;
		public ObjectInputStream in = null;
		JTextArea textArea;
		Document model;
		public Boolean running;
		private ContactsList contactsList;
		FriendsPanel participants;
		int offset = 0;

		GroupConversationClient(Socket socket, FriendsPanel participants) {
			this.participants = participants;
			try {
				contactsList = new ContactsList();
				this.socket = socket;
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				code = code.randomUUID();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setTextArea(JTextArea textArea) {
			this.textArea = textArea;
			this.model = this.textArea.getDocument();
		}

		public ContactsList getContactsList() {
			return contactsList;
		}

		public void setContactsList(ContactsList contactsList) {
			this.contactsList = contactsList;
		}

		public UUID getCode() {
			return code;
		}
		
		public synchronized void updateInt(String text)
		{
			try {
				model.insertString(offset, text, null);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset = offset + text.length();
		}

		public void Send(String text) throws BadLocationException {
			Message output = new Message(account.getScreenName() + ":" + text
					+ "\n", account.toContact(), null);
			try {
				out.writeObject(output);
				out.flush();
				updateInt(account.getScreenName() + ":" + text
						+ "\n");
				textArea.updateUI();
			} catch (IOException z) {
				z.printStackTrace();
			}

		}

		public void end() {
			running = false;
		}

		public void run() {
			Message input = null;
			running = true;
			while (running) {
				try {
					input = (Message) in.readObject();
					updateInt(input.getText());
					textArea.updateUI();
					ContactsList contactsList = input.getContacts();
					if (contactsList!=null)
					{
						participants.replaceFriends(contactsList);
						participants.updateUI();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					running = false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					running = false;
				}
			}
		}
	}

	class GroupConversationServer extends Thread {
		private UUID code;
		private JTextArea textArea;
		private Document model;
		private ContactsList contacts;
		private Boolean running;
		private CommsSet commsSet = new CommsSet();
		private ContactsList participants = new ContactsList();
		private int offset = 0;

		GroupConversationServer(ContactsList contacts, JTextArea textArea) {
			this.textArea = textArea;
			code = code.randomUUID();
			this.contacts = contacts;
			participants.addContact(account.toContact());

		}

		GroupConversationServer(ContactsList contacts) {
			participants.addContact(account.toContact());
			code = code.randomUUID();
			this.contacts = contacts;
		}

		public void setTextArea(JTextArea textArea) {
			this.textArea = textArea;
			this.model = this.textArea.getDocument();
		}

		public UUID getCode() {
			return code;
		}
		

		
		public synchronized void updateInt(String text)
		{
			try {
				model.insertString(offset, text, null);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset = offset + text.length();
		}

		public void Send(String text) {
			Message output = new Message(account.getScreenName() + ":" + text
					+ "\n", account.toContact(), participants);
			updateInt(account.getScreenName() + ":" + text
					+ "\n");
			textArea.updateUI();

			for (int i = 0; i < commsSet.size(); i++) {
				try {
					commsSet.get(i).write(output);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void end() {
			running = false;
			for (int i = 0; i < commsSet.size(); i++) {
				try {
					commsSet.get(i).end();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void addNewFriend(Contact contact) {
			Comms newComms = new Comms(contact, commsSet);
			participants.addContact(contact);
			commsSet.add(newComms);
			newComms.start();
			Send("---------------A new user has joined the chat---------------");
		}

		class CommsSet {
			ArrayList<Comms> commsArrayList = new ArrayList<Comms>();

			public synchronized void add(Comms comms) {
				commsArrayList.add(comms);
			}

			public synchronized Comms get(int i) {
				return commsArrayList.get(i);
			}

			public synchronized int size() {
				return commsArrayList.size();
			}
		}

		class Comms extends Thread {
			public Socket socket = null;
			public ObjectOutputStream out = null;
			public ObjectInputStream in = null;
			public CommsSet commsSet = null;
			private Contact contact;

			Comms(Contact contact, CommsSet commsSet) {
				try {
					this.contact = contact;
					socket = new Socket(contact.getIP(), 70);
					in = new ObjectInputStream(socket.getInputStream());
					out = new ObjectOutputStream(socket.getOutputStream());
					this.commsSet = commsSet;
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public int available() {
				try {
					return in.available();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -1;
			}
			
			public Contact getContact()
			{
				return contact;
			}
			
			public void end()
			{
				running = false;
			}

			public void write(Message message) {
				try {
					out.writeObject(message);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public Message read() {
				Message input = null;
				try {
					input = (Message) in.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return input;
			}

			public void run() {
				running = true;
				while (running) {
					try {
						Message input = this.read();
						updateInt(input.getText());
						textArea.updateUI();
						for (int h = 0; h < commsSet.size(); h++) {
							if (!(commsSet.get(h).getContact().equals(this.getContact()))) {
								commsSet.get(h).write(input);
								h++;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}