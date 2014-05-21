package com.common.messages.ControlMessage;

import com.common.Account;
import com.common.Contact;

public class AddFriendRequestControlMessage extends ControlMessage {
	private static final long serialVersionUID = -8708232262173644103L;
	private Account account;
	private Contact contact;

    public AddFriendRequestControlMessage() {
    }

    public AddFriendRequestControlMessage(Account account, Contact contact) {
        this.account = account;
        this.contact = contact;
        setType(701);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
