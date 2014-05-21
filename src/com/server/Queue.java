package com.server;


import java.io.Serializable;
import java.util.LinkedList;

/*
 * This thread represents a queue of tasks which the server must perform.
 * @author Kev
 */
class Queue implements Cloneable, Serializable {

    private static final long serialVersionUID = -376380157408766222L;
    private LinkedList queue;
    private int QueueLength;
    private int numberOfThreads;

    public Queue(
            int QueueLength,
            int numberOfThreads) {
        this.QueueLength = QueueLength;
        this.numberOfThreads = numberOfThreads;
        queue = new LinkedList();
    }

    /**
     * This method is used to add a task to the queue. This method
     * is synchronized  because multiple thread have access to the Queue.
     * This prevents any two threads from executing the methods simultaneously.
     */
    public synchronized void add(Object object) {
        // Ensure that the number of tasks in the queue is less than the maximum
        if (queue.size() < QueueLength) {
            // Add a new task to the queue
            queue.addLast(object);
            // Wake up all threads waiting on a task from the queue
            notifyAll();
        }
    }
    
    public synchronized int size() {
    	return queue.size();
    }

    /**
     * This method is used to get the next task in the queue. This method
     * is synchronized  because multiple thread have access to the Queue.
     * This prevents any two threads from executing the methods simultaneously.
     */
    public synchronized Object get() {
        // Setup waiting on the Request Queue
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (Exception e) {
            }
        }

        // Return the item at the head of the queue
        return queue.removeFirst();
    }
}