package com.common.messages.ControlMessage;


/**
 * This is the message used to respond to a request to sign out. It is
 * sent from the server to the client.
 * @author Kev
 */
public class SignOutResponseControlMessage extends ControlMessage {

    private static final long serialVersionUID = -3393146825876896386L;
    private String result;

    public SignOutResponseControlMessage(String result) {
        this.result = result;
        setType(202);
    }
}