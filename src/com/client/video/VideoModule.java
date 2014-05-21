package com.client.video;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.common.Account;
import com.common.Contact;

public class VideoModule {
	Transmitter cf = new Transmitter();
	public void startTransmitter(Contact contact, String port)
	{
		cf.start(contact, port);
	}
	
	public void startReceiver(Contact contact, String port)
	{
		Receiver avReceive = new Receiver(contact, port);
		if (!avReceive.initialize()) {
		    System.err.println("Failed to initialize the sessions.");
		    System.exit(-1);
		}

		// Check to see if AVReceive2 is done.
		try {
		    while (!avReceive.isFinished())
			Thread.sleep(500);
		} catch (Exception e) {}
		cf.end();
	}
	
	public static void main (String [] args)
	{
		VideoModule videoModule = new VideoModule();
		Contact contact = new Contact("dgfdgd", "dfgdfgdfg", "136.206.18.84", true);
		videoModule.startTransmitter(contact, "2000");
	}
}
