package com.common.messages.ControlMessage;

import com.common.Account;

public class JoinResponseControlMessage extends ControlMessage {
	private static final long serialVersionUID = 6083918370768130892L;
    private Account account;
    private Boolean successful;

    public JoinResponseControlMessage(Account account, Boolean successful) {
        this.account = account;
        this.successful = successful;
        setType(502);
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
