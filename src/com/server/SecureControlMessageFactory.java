package com.server;

/*
 * This class represents a control message factory and also contains
 * some verification code for incoming messages.
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import com.common.Account;
import com.common.Contact;
import com.common.ContactsList;
import com.common.messages.ControlMessage.AddFriendRequestControlMessage;
import com.common.messages.ControlMessage.AddFriendResponseControlMessage;
import com.common.messages.ControlMessage.ControlMessage;
import com.common.messages.ControlMessage.JoinRequestControlMessage;
import com.common.messages.ControlMessage.JoinResponseControlMessage;
import com.common.messages.ControlMessage.SignInRequestControlMessage;
import com.common.messages.ControlMessage.SignInResponseControlMessage;
import com.common.messages.ControlMessage.SignOutNotification;
import com.common.messages.ControlMessage.SignOutRequestControlMessage;
import com.common.messages.ControlMessage.UpdateFriendsListControlMessage;
import com.common.messages.NotificationMessage.FriendshipRequestMessage;
import com.common.messages.NotificationMessage.ResponseMessage;

public class SecureControlMessageFactory implements Cloneable, Serializable {

	private static final long serialVersionUID = 3494165669720004979L;
	private ControlMessage controlmessage;

	/*
	 * This method takes a message and depending on the type of this message
	 * returns another type of response message.
	 */
	public static ControlMessage getMessage(Object receivedObject,
			Database database) {
		ControlMessage receivedMessage = (ControlMessage) receivedObject;
		// check the type of the incomming message
		int messageType = receivedMessage.getType();
		final int signIn = 101;
		final int signOut = 201;
		final int join = 501;
		final int addFriend = 701;
		final int deleteFriend = 801;
		/*
		 * SignInRequestControlMessage This means that the incomming message is
		 * a request to sign in. A SignInResponseControlMessage must be
		 * returned.
		 */
		if (messageType == signIn) {
			SignInRequestControlMessage message = (SignInRequestControlMessage) receivedMessage;
			Account account = message.getAccount();

			// verify password
			if (database.isPassword(account.getScreenName(), account
					.getPassword())) {
				// set user as online and temporarily store their IP addresss
				database.setOnline(account.getScreenName(), account
						.getPassword(), account.getIP());
				System.out.println(account.getIP());
				Account returnAccount = database.getAccount(account
						.getScreenName(), account.getPassword());
				account.setID(database.getID(account.getScreenName(), account
						.getPassword()));
				Contact contact = account.toContact();
				contact.setOnline(true);
				sendIPAddressestoFriends(returnAccount.getContactsList(),
						contact);
				// return a success response message for type 1 messages.
				return new SignInResponseControlMessage(returnAccount, true);

			} else {
				// return a problem response message for type 1 messages.
				return new SignInResponseControlMessage(null, false);
			}
		} /*
		 * SignOutRequestControlMessage This means that the incomming message is
		 * a request to sign out. A SignOutResponseControlMessage must be
		 * returned.
		 */else if (messageType == signOut) {
			SignOutRequestControlMessage message = (SignOutRequestControlMessage) receivedMessage;
			Account account = message.getAccount();

			// verify password
			if (database.isPassword(account.getScreenName(), account
					.getPassword())) {
				database.setOffline(account.getScreenName(), account
						.getPassword());
				System.out.println(account.getIP());
				Account returnAccount = database.getAccount(account
						.getScreenName(), account.getPassword());
				account.setID(database.getID(account.getScreenName(), account
						.getPassword()));
				Contact contact = account.toContact();
				contact.setOnline(false);
				sendLogoutNotification(returnAccount.getContactsList(), contact);
			}
		}/*
		 * JoinRequestControlMessage This means that the incomming message is a
		 * request to join a new user. A JoinResponseControlMessage must be
		 * returned.
		 */else if (messageType == join) {
			JoinRequestControlMessage message = (JoinRequestControlMessage) receivedMessage;
			Account account = message.getAccount();

			// verify password
			if (!database.userExists(account.getScreenName())) {
				database.addUser(account.getScreenName(),
						account.getPassword(), account.getFtpPassword());
				account.setID(database.getID(account.getScreenName(),
						account.getPassword()).toString());
				return new JoinResponseControlMessage(account, true);
			} else {
				return new JoinResponseControlMessage(account, false);
			}
		}/*
		 * AddFriendRequestControlMessage This means that the incomming message
		 * is a request to add a new friend. A AddFriendResponseControlMessage
		 * must be returned.
		 */else if (messageType == addFriend) {
			AddFriendRequestControlMessage message = (AddFriendRequestControlMessage) receivedMessage;
			Account account = message.getAccount();
			Contact contact = message.getContact();
			System.out.println("begin:" + account.getIP());

			// verify passwordryuru
			if (database.userExists(account.getScreenName())) {
				database.addFriend(account.getScreenName(), contact
						.getScreenName(), account.getPassword());
				contact.setIP(database.getIP(contact.getScreenName()));
				Account otherAccount = requestFriendship(account, contact);
				database.addFriend(otherAccount.getScreenName(), account
						.getScreenName(), otherAccount.getPassword());

				System.out.println(account.getScreenName() + " " + account.getPassword() + " " + account.getIP() + " " + account.getID() + "/n/r");
				System.out.println(otherAccount.getScreenName() + " " + otherAccount.getPassword() + " " + otherAccount.getIP() + otherAccount.getID());
				
				account.setID(database.getID(account.getScreenName(), account
						.getPassword()));
				otherAccount.setID(database.getID(otherAccount.getScreenName(),
						otherAccount.getPassword()));
				
				System.out.println(account.getScreenName() + " " + account.getPassword() + " " + account.getIP() + " " + account.getID() + "/n/r");
				System.out.println(otherAccount.getScreenName() + " " + otherAccount.getPassword() + " " + otherAccount.getIP() + otherAccount.getID());

				AddFriendtoFriends(otherAccount.toContact(), account
						.toContact());
				AddFriendtoFriends(account.toContact(), otherAccount
						.toContact());
				ResponseMessage responseMessage = new ResponseMessage(account, true);
				responseMessage.setType(702);
				return responseMessage;
			} else {
				ResponseMessage responseMessage = new ResponseMessage(account, false);
				responseMessage.setType(702);
				return responseMessage;
			}
		}
		/*
		 * AddFriendRequestControlMessage This means that the incomming message
		 * is a request to add a new friend. A AddFriendResponseControlMessage
		 * must be returned.
		 */else if (messageType == deleteFriend) {
			ControlMessage message = (ControlMessage) receivedMessage;
			Account account = message.getAccount();
			Contact contact = message.getContact();
			System.out.println("deleteFriend:");

			// verify password
			if (database.userExists(account.getScreenName())) {
				database.deleteFriend(account.getScreenName(), contact
						.getScreenName(), account.getPassword());
				String otherUser = database.getIP(contact.getScreenName());

				account.setID(database.getID(account.getScreenName(), account
						.getPassword()));

				deleteFriendFromFriends(otherUser, account
						.toContact());
				ResponseMessage responseMessage = new ResponseMessage(account, true);
				responseMessage.setType(802);
				return responseMessage;
			} else {
				ResponseMessage responseMessage = new ResponseMessage(account, false);
				responseMessage.setType(802);
				return responseMessage;
			}
		}
		return null;
	}

	/*
	 * request friendship with another user
	 */
	public static Account requestFriendship(Account account, Contact contact) {
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		ResponseMessage response = null;
		if (contact.isOnline()) {
			try {
				clientSocket = new Socket(contact.getIP(), 465);
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in = new ObjectInputStream(clientSocket.getInputStream());

				FriendshipRequestMessage message = new FriendshipRequestMessage(
						account, contact);
				out.writeObject(message);

				response = (ResponseMessage) in.readObject();

				in.close();
				out.close();
				clientSocket.close();
			} catch (Exception d) {
				d.printStackTrace();
			}
		}
		return response.getAccount();
	}

	/*
	 * Send contact information to a user's friends.
	 */
	public static void sendIPAddressestoFriends(ContactsList contactsList,
			Contact contact) {
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		for (int i = 0; i < contactsList.size(); i++) {
			try {
				if (contactsList.get(i).isOnline()) {
					clientSocket = new Socket(contactsList.get(i).getIP(), 50);
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					in = new ObjectInputStream(clientSocket.getInputStream());

					ControlMessage output = new UpdateFriendsListControlMessage(
							contact);
					out.writeObject(output);

					in.close();
					out.close();
					clientSocket.close();
				}
			} catch (Exception d) {
				d.printStackTrace();
			}
		}
	}

	/*
	 * Add friends
	 */
	public static void AddFriendtoFriends(Contact sendTo, Contact contact) {
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			clientSocket = new Socket(sendTo.getIP(), 50);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());

			ControlMessage output = new ControlMessage(contact);
			output.setType(701);
			out.writeObject(output);

			in.close();
			out.close();
			clientSocket.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
	}
	
	/*
	 * Add friends
	 */
	public static void deleteFriendFromFriends(String sendTo, Contact contact) {
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			clientSocket = new Socket(sendTo, 50);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());

			ControlMessage output = new ControlMessage(contact);
			output.setType(901);
			out.writeObject(output);

			in.close();
			out.close();
			clientSocket.close();
		} catch (Exception d) {
			d.printStackTrace();
		}
	}

	/*
	 * Send a logout notification, for one user, to all of that user's friends.
	 */
	public static void sendLogoutNotification(ContactsList contactsList,
			Contact contact) {
		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		for (int i = 0; i < contactsList.size(); i++) {
			try {
				if (contactsList.get(i).isOnline()) {
					clientSocket = new Socket(contactsList.get(i).getIP(), 50);
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					in = new ObjectInputStream(clientSocket.getInputStream());

					ControlMessage output = new SignOutNotification(contact);
					out.writeObject(output);

					in.close();
					out.close();
					clientSocket.close();
				}
			} catch (Exception d) {
				d.printStackTrace();
			}
		}
	}
}