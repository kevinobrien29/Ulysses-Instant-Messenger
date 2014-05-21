package com.common;

/*
 * This class is used to store the information about the current user
 * of the client. It is also used to send user information to the server.
 * 
 * @author Kev
 */

import java.io.Serializable;
import java.util.UUID;

public class Account implements Cloneable, Serializable {

    private static final long serialVersionUID = -3581615399048940971L;
    // uniquely identifies a user
    private String iD;
    // a name which is also unique for each user
    private String screenName;
    // the IP address of the user
    private String iP;
    // the user's password
    private String password;
    // ftp password
    private String ftpPassword;
    // the user's contacts list
    private ContactsList contactsList;

    public Account() {
        super();
    }

    public Account(String screenName, String password, String ipAddress, String ftpPassword) {
        this.screenName = screenName;
        this.password = password;
        this.iP = ipAddress;
        this.ftpPassword = ftpPassword;
    }
    
    public Account(String ID, String screenName, ContactsList contactsList ,String ipAddress, String ftpPassword) {
        this.screenName = screenName;
        this.contactsList = contactsList;
        this.iD = ID;
        this.iP = ipAddress;
        this.ftpPassword = ftpPassword;
    }
    
    public Account(String screenName, String password, String ipAddress) {
        this.screenName = screenName;
        this.password = password;
        this.iP = ipAddress;
        this.ftpPassword = ftpPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public void updateContact(Contact contact) {
    	contactsList.updateContact(contact);
    }
	
	public void addContact(Contact contact) {
    	contactsList.addContact(contact);
    }
	
	public void removeoContact(Contact contact) {
		System.out.println("removing");
    	contactsList.removeoContact(contact);
    }
    
    public ContactsList getContactsList() {
        return contactsList;
    }

    public void setContactsList(ContactsList contactsList) {
        this.contactsList = contactsList;
    }

    public String getID() {
        return iD;
    }

    public void setID(String ID) {
        this.iD = ID;
    }
    
    public void setID(UUID ID) {
        this.iD = ID.toString();
    }
    
    public boolean isOnline(Contact contact)
    {
    	for (int i = 0; i < contactsList.size(); i++)
    	{
    		if (contactsList.get(i).getID().equals(contact.getID()))
    		{
    			if (contactsList.get(i).isOnline())
    			{
    				return true;
    			}
    			else
    			{
    				return false;
    			}
    		}
    	}
    	return false;
    }

    public String getIP() {
        return iP;
    }

    public void setIP(String ip) {
        iP = ip;
    }

    // returns each attribute except password as a string
    @Override
    public String toString() {
        return "Screen name: " + screenName + " ID: " + iD + "  " + iP + " \r ";
    }

    public Contact toContact()
    {
        return new Contact(screenName, iD, ftpPassword, iP, true);
    }
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}