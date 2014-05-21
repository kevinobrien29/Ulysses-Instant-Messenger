package com.common.messages.ControlMessage;

import com.common.Contact;

public class SignOutNotification extends ControlMessage{

    private static final long serialVersionUID = -6235042912546356225L;
    private Contact contact;

    public SignOutNotification() {
    }

    public SignOutNotification(Contact contact) {
        this.contact = contact;
        setType(501);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
