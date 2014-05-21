package com.server;


import java.util.ArrayList;

/**
 * This class represents the thread pool. It contains an array
 * of threads and a method to instantiate and launch them.
 * @author Kev
 */
public class ThreadPool {

    private static final long serialVersionUID = -7310293886939869363L;
    private int numberOfThreads;
    private Queue queue;
    // An array which represents the thread pool
    private ArrayList<LabourThreadServer> threadPool;
    // A database
    private Database database;
    private SecureControlMessageFactory messageFactory;

    ThreadPool(Database database, int numberOfThreads, Queue queue, SecureControlMessageFactory messageFactory) {
        this.database = database;
        this.numberOfThreads = numberOfThreads;
        this.threadPool = new ArrayList<LabourThreadServer>();
        this.queue = queue;
        this.messageFactory = messageFactory;
    }
    // create and start the threads
    public synchronized void createThreads() {
        // create the threads
        for (int i = 0; i < numberOfThreads; i++) {
            LabourThreadServer thread = new LabourThreadServer(queue, database, messageFactory);
            thread.setDaemon(true);
            // start the threads
            thread.start();
            // add the new thread to the array
            this.threadPool.add(thread);
        }
    }
    
    public synchronized int size() {
    	return threadPool.size();
    }
    
    public synchronized void addThread() {
        // create the thread
            LabourThreadServer thread = new LabourThreadServer(queue, database, messageFactory);
            thread.setDaemon(true);
            // start the thread
            thread.start();
            // add the new thread to the array
            this.threadPool.add(thread);
    }
    
    public synchronized void removeThread() {
        // create the threads
    	for (int i = 0; i < threadPool.size(); i++)
    	{
            if (!threadPool.get(i).isWorking())
            {
            	LabourThreadServer thread = this.threadPool.remove(i);
            	try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	return;
            }
    	}
    }
}
