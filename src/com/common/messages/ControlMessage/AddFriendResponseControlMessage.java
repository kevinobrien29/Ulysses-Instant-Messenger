package com.common.messages.ControlMessage;

import com.common.Account;
import com.common.Contact;

public class AddFriendResponseControlMessage extends ControlMessage {
	private static final long serialVersionUID = 6083918370768130892L;
    private Account account;
    private Boolean successful;

    public AddFriendResponseControlMessage(Account account, Boolean successful) {
        this.account = account;
        this.successful = successful;
        setType(702);
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
