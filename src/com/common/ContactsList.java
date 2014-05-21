package com.common;


/**
 * This class contains a user's contacts list. It is used to send a list
 * of contacts over the network and to store contact information for friends.
 * @author Kev
 */
import java.io.Serializable;
import java.util.ArrayList;

public class ContactsList implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2854111944073887384L;
	private ArrayList<Contact> contacts;

    public ContactsList() {
    	contacts = new ArrayList();
    }
    
    public ContactsList(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContactsList() {
        return contacts;
    }
    
    public Contact get(int i) {
        return contacts.get(i);
    }
    
    public void updateContact(Contact contact) {
    	for (int i = 0; i < contacts.size(); i++)
    	{
    		System.out.println("attempting addition");
    		if (contacts.get(i).getID().equals(contact.getID()))
    		{
    			contacts.set(i, contact);
    			return;
    		}
    	}
    	
    }
    
    public void addNewContact(Contact contact) {
    	contacts.add(contact);
    }
    
    public void removeoContact(Contact contact) {
    	for (int i = 0; i < contacts.size(); i++)
    	{
    		if (contacts.get(i).equals(contact))
    		{
    			contacts.remove(i);
    			return;
    		}
    	}
    }
    
    public int size() {
        return contacts.size();
    }

    public void setContacts(ArrayList contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    // return each contact as a string
    @Override
    public String toString() {
        String tmp = "";
        for (int i = 0; i < contacts.size(); i++) {
            tmp = tmp + contacts.get(i).toString() + "\r";
        }
        return tmp;
    }
}