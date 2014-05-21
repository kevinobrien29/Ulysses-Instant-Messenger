/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.common.Contact;
import com.common.ContactsList;



/**
 *
 * @author Kev
 */
public class FriendsList extends JTree
{

    private DefaultMutableTreeNode top;
    private DefaultTreeModel model;

    public FriendsList()
    {
        top = new DefaultMutableTreeNode("Contacts");
        model = new DefaultTreeModel(top);
        this.setModel(model);
    }

    public void addContact(Contact contact)
    {
        DefaultMutableTreeNode contactNode = new DefaultMutableTreeNode(contact);
        model.insertNodeInto(contactNode, top, 0);
        this.updateUI();
    }

    public void Load(ContactsList contactsList)
    {
    	top.removeAllChildren();
        for (int i = 0; i < contactsList.size(); i++) {
            addContact(contactsList.get(i));
        }
        this.updateUI();
    }
}
