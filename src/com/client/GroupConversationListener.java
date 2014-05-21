package com.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

class GroupConversationListener extends Thread {
	private Messenger messenger;
	Boolean running;

	GroupConversationListener(Messenger messenger) {
		this.messenger = messenger;
	}

	public void end() {
		running = false;
	}
	
	public void run() {
		running = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(70);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Socket socket = null;
		while (running)
		{
			try {
				
				socket = serverSocket.accept();
				System.out.println("accepted");
				System.out.println("accepted");
				FriendsPanel users = new FriendsPanel(messenger);
				UUID code = messenger.addGroupConversationClient(socket, users);
			    GroupConversationWindow groupConversationWindow = new GroupConversationWindow(messenger, code, users);
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}