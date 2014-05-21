package com.client;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * A 1.4 application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.client.video.VideoModule;
import com.common.Account;
import com.common.Contact;

public class GUI extends JFrame implements TreeSelectionListener, MouseListener {

	private FriendsList friendsList;
	private Account account;
	private Messenger messenger;
	private FtpServerModule serverModule;
	private VideoModule videoModule;
	private Startup startUp;

	public GUI(final Messenger messenger, Startup startUp) {
		this.startUp = startUp;
		JPanel panel = new JPanel();
		this.messenger = messenger;
		this.account = messenger.getAccount();
		friendsList = new FriendsList();
		friendsList.setCellRenderer(new IconRenderer());
		VideoChatListener videoChatListener = new VideoChatListener();
		videoChatListener.start();
		AddFriendListener addFriendListener = new AddFriendListener(messenger,
				account);
		addFriendListener.start();

		friendsList.Load(account.getContactsList());

		serverModule = new FtpServerModule(account.toContact());
		messenger.startConversationListener(account);
		serverModule.configure();
		serverModule.startServer();

		MenuPanel menuPanel = new MenuPanel();

		// Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(friendsList);
		// Listen for when the selection changes.
		friendsList.addTreeSelectionListener(this);
		friendsList.addMouseListener(this);

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(friendsList);
		treeView.setPreferredSize(new Dimension(200, 300));

		messenger.setFriendsList(friendsList);

		// Add the scroll pane to this panel.
		

		// Create and set up the window.
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		Menu menuBar = this.new Menu();
		this.setJMenuBar(menuBar.getJMenuBar());
		panel.setLayout(new BorderLayout());
		panel.add(treeView, BorderLayout.CENTER);
		panel.add(menuPanel, BorderLayout.PAGE_END);
		this.setContentPane(panel);

		// Display the window.
		this.pack();
		this.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			TreePath closestTreePath = friendsList.getClosestPathForLocation(e
					.getX(), e.getY());
			if (closestTreePath != null) {
				friendsList.setSelectionPath(closestTreePath);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) closestTreePath
						.getLastPathComponent();
				if (node == null) {
					return;
				}
				if (node.isRoot()) {
					return;
				}
				Object nodeInfo = node.getUserObject();
				if (node.isLeaf()) {
					Contact contact = (Contact) nodeInfo;
					PopupMenu popupMenu = new PopupMenu(contact);
					popupMenu.maybeShowPopup(e);
				} else {
					return;
				}
			}
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			TreePath closestTreePath = friendsList.getClosestPathForLocation(e
					.getX(), e.getY());
			if (closestTreePath != null) {
				friendsList.setSelectionPath(closestTreePath);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) closestTreePath
						.getLastPathComponent();
				if (node == null) {
					return;
				}
				if (node.isRoot()) {
					return;
				}
				Object nodeInfo = node.getUserObject();
				if (node.isLeaf() && ((Contact) nodeInfo).isOnline()) {
					Contact contact = (Contact) nodeInfo;
					ConversationWindow conversationWindow = new ConversationWindow(
							contact, messenger, account);
					conversationWindow.setTitle(contact.getScreenName());
				} else {
					return;
				}
			}
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			TreePath closestTreePath = friendsList.getClosestPathForLocation(e
					.getX(), e.getY());
			if (closestTreePath != null) {
				friendsList.setSelectionPath(closestTreePath);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) closestTreePath
						.getLastPathComponent();
				if (node == null) {
					return;
				}
				if (node.isRoot()) {
					return;
				}
				Object nodeInfo = node.getUserObject();
				if (node.isLeaf() && ((Contact) nodeInfo).isOnline()) {
					Contact contact = (Contact) nodeInfo;
					ConversationWindow conversationWindow = new ConversationWindow(
							contact, messenger, account);
					conversationWindow.setTitle(contact.getScreenName());
				} else {
					return;
				}
			}
		}
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			// System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	class IconRenderer extends DefaultTreeCellRenderer {

		public IconRenderer() {
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			if (leaf) {
				DefaultMutableTreeNode contactNode = (DefaultMutableTreeNode) value;
				if (!(contactNode.getUserObject() instanceof String)) {
					Contact contact = (Contact) contactNode.getUserObject();
					Image img;

					if (account.isOnline(contact)) {
						img = Toolkit
								.getDefaultToolkit()
								.getImage(
										Thread
												.currentThread()
												.getContextClassLoader()
												.getResource(
														"\\icons\\MediaView\\userOnline.png"));
					} else {
						img = Toolkit
								.getDefaultToolkit()
								.getImage(
										Thread
												.currentThread()
												.getContextClassLoader()
												.getResource(
														"\\icons\\MediaView\\userOffline.png"));
					}
					ImageIcon icon = new ImageIcon(img);
					setIcon(icon);
				}
			} else {
				Image img = Toolkit.getDefaultToolkit().getImage(
						Thread.currentThread().getContextClassLoader()
								.getResource(
										"\\icons\\tree\\topLevel.png"));
				ImageIcon icon = new ImageIcon(img);
				setIcon(icon);
			}

			return this;
		}
	}

	public class Menu implements ActionListener {

		private JMenuBar menuBar;
		private JMenu menu, submenu;
		private JMenuItem menuItem;
		private JRadioButtonMenuItem rbMenuItem;
		private JCheckBoxMenuItem cbMenuItem;

		Menu() {
			// Create the menu bar.
			menuBar = new JMenuBar();

			// Build the first menu.
			menu = new JMenu("Menu");
			menu.setMnemonic(KeyEvent.VK_A);
			menuBar.add(menu);

			// a group of JMenuItems
			menuItem = new JMenuItem("Add contact", KeyEvent.VK_T);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
					ActionEvent.ALT_MASK));
			menuItem.getAccessibleContext().setAccessibleDescription(
					"Add a contact to your contacts list");
			menuItem.setActionCommand("Add contact");
			menuItem.addActionListener(this);
			menu.add(menuItem);

			menuItem = new JMenuItem("Start Group Conversation", KeyEvent.VK_B);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
					ActionEvent.ALT_MASK));
			menuItem.setActionCommand("group conversation");
			menuItem.addActionListener(this);
			menu.add(menuItem);

			// a group of radio button menu items
			menu.addSeparator();
			ButtonGroup group = new ButtonGroup();
		}

		public JMenuBar getJMenuBar() {
			return menuBar;
		}

		public void actionPerformed(ActionEvent e) {
			if ("Add contact".equals(e.getActionCommand())) {
				AddContactWindow addContactWindow = new AddContactWindow(
						account, messenger);
				addContactWindow.setVisible(true);
				addContactWindow.pack();
			} else if ("group conversation".equals(e.getActionCommand())) {
				GroupConversationWindow groupConversationWindow = new GroupConversationWindow(
						account.getContactsList(), messenger);
			}
		}
	}

	class MenuPanel extends JPanel implements ActionListener{
		private JPanel panel;
		private GridLayout buttonLayout = new GridLayout();
		private int rows = 1;
		private static final int COLS = 2;

		public MenuPanel() {
			setLayout(new BorderLayout());
			panel = new JPanel();
			buttonLayout.setColumns(COLS);
			buttonLayout.setRows(rows);
			panel.setLayout(buttonLayout);
			Image img = Toolkit.getDefaultToolkit().getImage(
					Thread.currentThread().getContextClassLoader().getResource(
							"\\icons\\Bar\\conversation.png"));
			ImageIcon icon = new ImageIcon(img);
			JButton button = new JButton("", icon);
			button.setToolTipText("Add Friend");
			button.setActionCommand("start conversation");
			button.addActionListener(this);
			panel.add(button);
			img = Toolkit.getDefaultToolkit().getImage(
					Thread.currentThread().getContextClassLoader().getResource(
							"\\icons\\Bar\\groupConversation.png"));
			icon = new ImageIcon(img);
			button = new JButton("", icon);
			button.setToolTipText("Group Chat");
			button.setActionCommand("group conversation");
			button.addActionListener(this);
			panel.add(button);
			Dimension buttonPanelDimension = new Dimension(200, 70);
			panel.setSize(buttonPanelDimension);
			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setPreferredSize(buttonPanelDimension);
			add(scrollPane);
		}
		
		public void actionPerformed(ActionEvent e) {
			if ("start conversation".equals(e.getActionCommand())) {
				AddContactWindow addContactWindow = new AddContactWindow(
						account, messenger);
				addContactWindow.setVisible(true);
				addContactWindow.pack();
			}
			else if ("group conversation".equals(e.getActionCommand())) {
				GroupConversationWindow groupConversationWindow = new GroupConversationWindow(
						account.getContactsList(), messenger);
			}
		}
	}

	public class PopupMenu extends JPopupMenu implements ActionListener {

		private JPopupMenu popupMenu;
		private JMenuItem menuItem;
		private Contact contact;

		PopupMenu(Contact contact) {
			this.contact = contact;
			popupMenu = new JPopupMenu();
			menuItem = new JMenuItem("view user media");
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
			menuItem = new JMenuItem("Start Video Chat", KeyEvent.VK_B);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
					ActionEvent.ALT_MASK));
			menuItem.setActionCommand("video chat");
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
			menuItem = new JMenuItem("delete contact", KeyEvent.VK_B);
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
					ActionEvent.ALT_MASK));
			menuItem.setActionCommand("delete contact");
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
		}

		public JPopupMenu getJPopupMenu() {
			return popupMenu;
		}

		public void actionPerformed(ActionEvent e) {
			if ("view user media".equals(e.getActionCommand()) && contact.isOnline()) {
				MediaView mediaView = new MediaView(contact);
				mediaView.setSize(500, 500);
				mediaView.setVisible(true);
				mediaView.connect();
			} else if ("video chat".equals(e.getActionCommand()) && contact.isOnline()) {
				try {
					Socket connector = new Socket(contact.getIP(), 300);
					ObjectOutputStream out = new ObjectOutputStream(connector
							.getOutputStream());
					Contact localContact = account.toContact();
					out.writeObject(localContact);
					System.out.println(localContact.getIP()
							+ localContact.getID());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				videoModule = new VideoModule();
				Runnable r = new Runnable() {
					public void run() {
						videoModule.startReceiver(contact, "2005");
					}
				};
				Thread d = new Thread(r);
				d.start();
				Runnable w = new Runnable() {
					public void run() {
						videoModule.startTransmitter(contact, "2000");
					}
				};
				Thread f = new Thread(w);
				f.start();
			} else if ("delete contact".equals(e.getActionCommand())) {
				messenger.deleteFriend(account, contact);
			}
		}

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private class AddCategoryWindow extends JPanel implements ActionListener {

		private JFrame frame;

		// Labels to identify the fields
		private JLabel nameLabel;
		private JButton oK;
		private JButton cancel;

		// Strings for the labels
		private String nameString = "Name: ";

		// Fields for data entry
		private JTextField nameField;

		public AddCategoryWindow() {
			super(new BorderLayout());

			// Create the labels.
			nameLabel = new JLabel(nameString);

			// Create the text fields and set them up.
			nameField = new JTextField();
			nameField.setColumns(10);

			// Tell accessibility tools about label/textfield pairs.
			nameLabel.setLabelFor(nameField);

			oK = new JButton("OK");
			oK.setVerticalTextPosition(SwingConstants.BOTTOM);
			oK.setHorizontalTextPosition(SwingConstants.RIGHT);
			oK.setMnemonic(KeyEvent.VK_M);
			oK.setActionCommand("ok");
			oK.addActionListener(this);

			cancel = new JButton("Cancel");
			cancel.setVerticalTextPosition(SwingConstants.BOTTOM);
			cancel.setHorizontalTextPosition(SwingConstants.LEFT);
			cancel.setMnemonic(KeyEvent.VK_M);
			cancel.setActionCommand("cancel");
			cancel.addActionListener(this);

			// Lay out the labels in a panel.
			JPanel labelPane = new JPanel(new GridLayout(1, 1));
			labelPane.add(nameLabel);

			// Layout the text fields in a panel.
			JPanel fieldPane = new JPanel(new GridLayout(1, 1));
			fieldPane.add(nameField);

			JPanel buttonPane = new JPanel(new GridLayout(0, 3));
			buttonPane.add(oK);
			buttonPane.add(cancel);

			// Put the panels in this panel, labels on left,
			// text fields on right.
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

			add(labelPane, BorderLayout.LINE_START);
			add(fieldPane, BorderLayout.CENTER);
			add(buttonPane, BorderLayout.PAGE_END);
		}

		/**
		 * Create the GUI and show it. For thread safety, this method should be
		 * invoked from the event dispatch thread.
		 */
		public void actionPerformed(ActionEvent e) {
			if ("ok".equals(e.getActionCommand())) {
				String newString = nameField.getText();
			} else if ("cancel".equals(e.getActionCommand())) {
			}
		}

		public void init() {
			// Create and set up the window.
			frame = new JFrame("Add Category");

			// Add contents to the window.
			frame.add(new AddCategoryWindow());

			// Display the window.
			frame.pack();
			frame.setVisible(true);
		}
	}
}
