package com.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.client.video.VideoModule;
import com.common.Contact;

class VideoChatListener extends Thread {
	private Messenger messenger;
	private Boolean running;
	private VideoModule videoModule;

	VideoChatListener() {
		videoModule = new VideoModule();
	}

	public void end() {
		running = false;
	}
	
	public void run() {
		running = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(300);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Socket socket = null;
		while (running)
		{
			videoModule = new VideoModule();
			try {
				
				socket = serverSocket.accept();
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				final Contact contact = (Contact) in.readObject();
				Runnable r = new Runnable() {
					public void run() {
						videoModule.startTransmitter(contact, "2005");
					}
				};
				Thread t = new Thread(r);
				t.start();
				Runnable w = new Runnable() {
					public void run() {
						videoModule.startReceiver(contact, "2000");
					}
				};
				Thread f = new Thread(w);
				f.start();
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}