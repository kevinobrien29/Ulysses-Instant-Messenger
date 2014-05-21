package com.common.messages.NotificationMessage;

import java.io.Serializable;

import com.common.Account;
import com.common.Contact;
import com.common.messages.ControlMessage.ControlMessage;

public class ResponseMessage extends ControlMessage implements Serializable{
	private static final long serialVersionUID = -8708232262173644103L;
	private Account account;
	private Contact contact;
	private boolean success;

    public ResponseMessage() {
    }

    public ResponseMessage(Account account, Contact contact, boolean success) {
        this.account = account;
        this.contact = contact;
        this.success = success;
    }
    
    public ResponseMessage(Account account, boolean success) {
        this.account = account;
        this.success = success;
    }
    
    public ResponseMessage(Contact contact) {
        this.contact = contact;
    }
    public ResponseMessage(Boolean success) {
        this.success = success;
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

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
