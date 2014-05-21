package com.common.messages.ControlMessage;

import com.common.Account;
import com.common.ContactsList;


/**
 * This is the message used to respond to a sign in request. It is
 * sent from the server to the client.
 * @author Kev
 */
public class SignInResponseControlMessage extends ControlMessage {

    private static final long serialVersionUID = 6083918370768130892L;
    private Account account;
    private Boolean successful;

    public SignInResponseControlMessage(Account account, Boolean successful) {
        this.account = account;
        this.successful = successful;
        setType(102);
    }
    
	public Account getAccount()
    {
        return account;
    }
    
    public Boolean isSuccessfull()
    {
        return successful;
    }
}
