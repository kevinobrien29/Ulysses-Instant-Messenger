package com.common;


/**
 * This class contains the contact information for one friend. It contains
 * fields which will enable the user to see their friend's name and data
 * to enable the client to make a connection to this contact's client.
 * @author Kev
 */
import java.io.Serializable;

public class Contact implements Cloneable, Serializable {

    private static final long serialVersionUID = 4609237605059759610L;
    // IP address of contact
    private String iP;
    // ID of contact (used by the server)
    private String iD;
    // Shoes if the contact is online or not
    private Boolean online;
    // User's screen name
    private String screenName;
    // password for the contacts ftp server to allow media transfer
    private String ftpPassword;

    public Contact() {
    }


    public Contact(String screenName, String id, String ip, Boolean online) {
        this.iP = ip;
        this.iD = id;
        this.screenName = screenName;
        this.online = online;
    }
    
    public Contact(String screenName, String id, String ftpPassword, String ip, Boolean online) {
        this.iP = ip;
        this.iD = id;
        this.screenName = screenName;
        this.online = online;
        this.ftpPassword= ftpPassword;
    }
    
    public Contact(String screenName, String id, String ip, String ftpPassword) {
        this.iP = ip;
        this.iD = id;
        this.screenName = screenName;
        this.ftpPassword= ftpPassword;
    }
    
    public Boolean equals(Contact contact) {
        if (this.iD.equals(contact.getID()))
        {
        	return true;
        }
        return false;
    }

    public String getFtpPassword() {
		return ftpPassword;
	}


	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}


	public String getID() {
        return iD;
    }

    public void setID(String screenName) {
        this.iD = screenName;
    }

    public String getIP() {
        return iP;
    }

    public void setIP(String ip) {
        iP = ip;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public String toString() {
        return screenName;
    }
}