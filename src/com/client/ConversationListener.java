package com.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import com.common.Account;

class ConversationListener extends Thread {
	private Messenger messenger;
	private Boolean running;
	private Account account;

	ConversationListener(Messenger messenger, Account account) {
		this.messenger = messenger;
		this.account = account;
	}

	public void end() {
		running = false;
	}
	
	public void run() {
		running = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(30);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Socket socket = null;
		while (running)
		{
			try {
				
				socket = serverSocket.accept();
				UUID code = messenger.addConversation(socket);
			    ConversationWindow conversationWindow = new ConversationWindow(messenger, code, account);
	
			} catch (IOException e) {
				System.err.println("Could not listen on port: 30");
				e.printStackTrace();
			}
		}
	}
}