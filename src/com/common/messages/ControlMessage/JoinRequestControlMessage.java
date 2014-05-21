package com.common.messages.ControlMessage;

import com.common.Account;

public class JoinRequestControlMessage extends ControlMessage {
	private static final long serialVersionUID = -8708232262173644103L;
	private Account account;

    public JoinRequestControlMessage() {
    }

    public JoinRequestControlMessage(Account account) {
        this.account = account;
        setType(501);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
