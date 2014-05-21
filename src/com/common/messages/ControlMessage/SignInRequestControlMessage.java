package com.common.messages.ControlMessage;
import com.common.Account;


/**
 * This is the message used to request a sign in operation for a user. It is
 * sent from the client to the server.
 * @author Kev
 */
public class SignInRequestControlMessage extends ControlMessage implements Cloneable {

    private static final long serialVersionUID = -8708232262173644103L;
    private Account account;

    public SignInRequestControlMessage() {
    }

    public SignInRequestControlMessage(Account account) {
        this.account = account;
        setType(101);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
