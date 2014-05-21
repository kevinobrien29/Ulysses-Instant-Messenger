package com.server;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import com.common.messages.ControlMessage.ControlMessage;

/**
 * This thread performs the tasks in the queue
 * @author Kev
 */
class LabourThreadServer extends Thread implements Cloneable, Serializable {

    private static final long serialVersionUID = -7239275510402369847L;
    // The queue of tasks
    private Queue queue;
    private boolean looping;
    private Database database;
    // The message Factory which uses the Factory Method pattern
    private SecureControlMessageFactory messageFactory;
    private boolean working = false;

    public LabourThreadServer(Queue queue, Database database, SecureControlMessageFactory messageFactory) {
        this.queue = queue;
        this.database = database;
        this.messageFactory = messageFactory;
    }
    public void end()
    {
    	looping = false;
    }
    public boolean isWorking()
    {
    	return working;
    }
    
    // The threads main section
    @Override
    public void run() {
        Object input;
        ControlMessage output;
        looping = true;
        while (looping) {
            try {

                // get the next object from the queue or sleep
                Object object = queue.get();
                if (looping)
                {
	                working = true;
	                Socket clientSocket = (Socket) object;
	                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
	                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
	                // read in message from the socket
	                input = in.readObject();
	                // get a response message from the factory
	                output = messageFactory.getMessage(input, database);
	                // send the message back over the socket
	                out.writeObject(output);
	                out.close();
	                in.close();
	                clientSocket.close();
                }
                working = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

