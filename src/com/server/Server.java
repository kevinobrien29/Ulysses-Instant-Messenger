package com.server;

/**
 * This is the main class in the application. It launches all other classes.
 * @author Kev
 */
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private static final long serialVersionUID = -5614949212095556654L;
    private static ServerSocket serverSocket;
    // The port which the server should listen on
    private int port;
    // The maximum length of the queue
    private int queueLength;
    // The maximum number of threads
    private int numberOfThreads;
    private Queue queue;
    // An class which represents the thread pool
    private ThreadPool threadPool;
    // A database
    private Database database;
    private SecureControlMessageFactory messageFactory;
    private boolean running = true;
    private int addThreshold = 50;
    private int removeThreshold = 5;

    Server(int queueLength, int numberOfThreads, int port) {
        this.port = port;
        this.setQueueLength(queueLength);
        this.database = new Database();
        this.setNumberOfThreads(numberOfThreads);
        this.queue = new Queue(queueLength, numberOfThreads);
        this.messageFactory = new SecureControlMessageFactory();
        this.threadPool = new ThreadPool(database, numberOfThreads, queue, messageFactory);
        running = true;
    }
    //Starts the server

    public void Launch() {
        threadPool.createThreads();
        serverSocket = null;
        // Create the threads
        try {
            serverSocket = new ServerSocket(port);

            // loop forever
            while (true) {
                Socket clientSocket = null;
                //Listen for incomming connections
                clientSocket = serverSocket.accept();
                //Add to queue
                queue.add(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // set the length of the queue of tasks

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }
    // get the length of the queue of tasks

    public int getQueueLength() {
        return queueLength;
    }
    // get the number of the threads which were added to the threadPool

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    // set the number of the threads which were added to the threadPool

    public int getNumberOfThreads() {
        return numberOfThreads;
    }
    
    public void run()
    {
    // Add or remove threads derpending on network traffic.
    	while (running)
    	{
    		while (queue.size() > addThreshold && threadPool.size() < removeThreshold)
    		{
    			threadPool.addThread();
    		}
    		while (queue.size() < removeThreshold && threadPool.size() > addThreshold)
    		{
    			threadPool.removeThread();
    		}
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public static void main(String[] args) {
        Server server = new Server(500, 5, 20);
        System.out.println("Server Online");
        server.Launch();
        
    }
}