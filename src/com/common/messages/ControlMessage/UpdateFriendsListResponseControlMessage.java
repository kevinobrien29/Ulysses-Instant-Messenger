package com.common.messages.ControlMessage;

import com.common.ContactsList;


/**
 * This is the message used to respond to a sign in request. It is
 * sent from the server to the client.
 * @author Kev
 */
public class UpdateFriendsListResponseControlMessage extends ControlMessage {

    private ContactsList contactsList;
    private Boolean successful;

    public UpdateFriendsListResponseControlMessage(ContactsList contactsList, Boolean successful) {
        this.contactsList = contactsList;
        this.successful = successful;
        setType(302);
    }
    
    public ContactsList getContactsList()
    {
        return contactsList;
    }
    
    public Boolean isSuccessfull()
    {
        return successful;
    }
}