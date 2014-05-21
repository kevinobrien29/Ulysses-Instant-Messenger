package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.common.Account;
import com.common.Contact;
import com.common.messages.NotificationMessage.FriendshipRequestMessage;
import com.common.messages.NotificationMessage.ResponseMessage;

public class AddFriendListener extends Thread {
	private Messenger messenger;
	private Boolean running;
	private Account account;
	private Contact contact;

	AddFriendListener(Messenger messenger, Account account) {
		this.messenger = messenger;
		this.account = account;
	}

	public void end() {
		running = false;
	}

	public void run() {
		running = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(465);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Socket socket = null;
		while (running) {
			try {

				socket = serverSocket.accept();
				ObjectInputStream in = new ObjectInputStream(socket
						.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket
						.getOutputStream());
				try {
					contact = ((FriendshipRequestMessage) in.readObject())
							.getAccount().toContact();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AddFriendQuestionDialog addFriendRequestDialog = new AddFriendQuestionDialog(out, contact, account);
				addFriendRequestDialog.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class AddFriendQuestionDialog {
		JFrame frame = null;
		String message = "message";
		Account account;
		Contact contact;
		ObjectOutputStream out;

		public AddFriendQuestionDialog(ObjectOutputStream out, Contact contact, Account account) {
			this.contact = contact;
			this.account = account;
			message = "Do you wish to become friends with "
					+ contact.getScreenName() + "?";
			frame = new JFrame();
			this.out = out;
		}

		public void start() {

			int answer = JOptionPane.showConfirmDialog(frame, message);
			try {
				if (answer == JOptionPane.YES_OPTION) {
					// User clicked YES.
					ResponseMessage output = new ResponseMessage(account, true);
					out.writeObject(output);
				} else if (answer == JOptionPane.NO_OPTION) {
					// User clicked NO.
					ResponseMessage output = new ResponseMessage(account, false);
					out.writeObject(output);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
