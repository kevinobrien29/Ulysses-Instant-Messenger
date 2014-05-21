package com.common.messages.ControlMessage;


/**
 * This is the superclass upon which all control message types are based.
 * All control messages extend this class.
 * @author Kev
 */
import java.io.Serializable;

import com.common.Account;
import com.common.Contact;

public class ControlMessage implements Cloneable, Serializable {

    private static final long serialVersionUID = 8801985283271757795L;
    // An integer which specifies the type of message
    private int type;
    
    private Account account;
    private Contact contact;

    public ControlMessage() {
    }
    
    public ControlMessage(Account account, Contact contact) {
        this.account = account;
        this.contact = contact;
    }
    
    public ControlMessage(Contact contact) {
        this.contact = contact;
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + type;
        return result;
    }

    // compare an instance of this class with another object
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ControlMessage other = (ControlMessage) obj;
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() {

        // Do the basic clone
        ControlMessage message = null;
        try {
            message = (ControlMessage) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen
            throw new InternalError(e.toString());
        }

        // Clone mutable members
        return message;
    }
}
