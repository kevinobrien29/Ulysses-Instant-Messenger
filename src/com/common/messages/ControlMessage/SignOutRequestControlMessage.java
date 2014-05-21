package com.common.messages.ControlMessage;

import com.common.Account;


/**
 * This is the message used to request a sign out operation for a user. It is
 * sent from the client to the server.
 * @author Kev
 */
public class SignOutRequestControlMessage extends ControlMessage implements Cloneable {

    private static final long serialVersionUID = -6235042912546356225L;
    private Account account;

    public SignOutRequestControlMessage() {
    }

    public SignOutRequestControlMessage(Account account) {
        this.account = account;
        setType(201);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((account == null) ? 0 : account.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SignOutRequestControlMessage other = (SignOutRequestControlMessage) obj;
        if (account == null) {
            if (other.account != null) {
                return false;
            }
        } else if (!account.equals(other.account)) {
            return false;
        }
        return true;
    }
}
