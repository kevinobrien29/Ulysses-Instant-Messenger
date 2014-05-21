package com.client;

import java.io.Serializable;

import com.common.Contact;
import com.common.ContactsList;

class Message implements Serializable {
	// Variables

	private String text;
	private Contact contact;
	private ContactsList contacts;

	public Message(String text, Contact contact) {
		this.text = text;
		this.contact = contact;
	}
	
	public Message(String text, Contact contact, ContactsList contacts) {
		this.contacts = contacts;
		this.text = text;
		this.contact = contact;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Contact getUserName() {
		return contact;
	}

	public void setUserName(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public ContactsList getContacts() {
		return contacts;
	}

	public void setContacts(ContactsList contacts) {
		this.contacts = contacts;
	}
}