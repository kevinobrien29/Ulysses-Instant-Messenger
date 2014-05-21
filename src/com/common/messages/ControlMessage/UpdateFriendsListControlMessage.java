package com.common.messages.ControlMessage;
import com.common.Account;
import com.common.Contact;


/**
 * This is the message used to request a sign in operation for a user. It is
 * sent from the client to the server.
 * @author Kev
 */
public class UpdateFriendsListControlMessage extends ControlMessage {

	private Contact contact;

    public UpdateFriendsListControlMessage() {
    }

    public UpdateFriendsListControlMessage(Contact contact) {
    	this.contact = contact;
        setType(301);
    }

    public Contact getContact() {
    	return contact;
    }

    public void setContact(Contact contact) {
    	this.contact = contact;
    }
}
