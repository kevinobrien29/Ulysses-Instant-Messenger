package com.client;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import com.common.Contact;

public class Startup extends JPanel implements ActionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Startup.class);

	private Messenger messenger;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JPanel panel = new JPanel();
	static JFrame frame;

	private static String userNameString = "username: ";
	private static String passwordString = "password: ";

	private JFormattedTextField userNameField;
	private JPasswordField passwordField;
	private JButton confirm;
	private JButton join;
	private GUI gui = null;

	public Startup(Messenger messenger) {
		this.setLayout(new BorderLayout());
		this.messenger = messenger;

		// Create the labels.
		userNameLabel = new JLabel(userNameString);
		passwordLabel = new JLabel(passwordString);
		// Create the text fields and set them up.
		userNameField = new JFormattedTextField();
		userNameField.setColumns(10);
		userNameField.setText("Username");

		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordField.setText("Password");

		// Tell accessibility tools about label/textfield pairs.
		userNameLabel.setLabelFor(userNameField);
		passwordLabel.setLabelFor(passwordField);

		confirm = new JButton("confirm");
		confirm.setVerticalTextPosition(AbstractButton.CENTER);
		confirm.setHorizontalTextPosition(AbstractButton.LEADING);

		join = new JButton("sign up");
		join.setVerticalTextPosition(AbstractButton.CENTER);
		join.setHorizontalTextPosition(AbstractButton.LEADING);

		join.setMnemonic(KeyEvent.VK_D);
		join.setActionCommand("sign up");
		join.addActionListener(this);

		confirm.setMnemonic(KeyEvent.VK_D);
		confirm.setActionCommand("confirm");
		confirm.addActionListener(this);

		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\Startup\\logo.png"));
		ImageIcon icon = new ImageIcon(img);
		JLabel icoon = new JLabel(icon);

		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(userNameLabel);
		labelPane.add(passwordLabel);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));
		fieldPane.add(userNameField);
		fieldPane.add(passwordField);

		JPanel buttonPane = new JPanel(new GridLayout(0, 1));
		buttonPane.add(confirm);
		buttonPane.add(join);

		// Put the panels in this panel, labels on left,
		// text fields on right.
		this.add(labelPane, BorderLayout.WEST);
		this.add(icoon, BorderLayout.NORTH);
		this.add(fieldPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (logger.isDebugEnabled()) {
			logger.debug("actionPerformed(ActionEvent) - start");
		}

		if ("confirm".equals(e.getActionCommand())) {
			confirm.disable();
			userNameField.disable();
			passwordField.disable();
			confirm.setText("cancel");
			confirm.setActionCommand("cancel");

			if (messenger.Login(userNameField.getText(), passwordField
					.getText())) {
				logger.info("password accepted");
				gui = new GUI(messenger, this);
				frame.setVisible(false);
				messenger.startGroupConversationListener();
				messenger.startUpdaterThread();
			} else {
				confirm.enable();
				userNameField.enable();
				passwordField.enable();
				confirm.setText("confirm");
				confirm.setActionCommand("confirm");
			}
		} else if ("cancel".equals(e.getActionCommand())) {
			confirm.enable();
			userNameField.enable();
			passwordField.enable();
			confirm.setText("confirm");
			confirm.setActionCommand("confirm");
		} else if ("sign up".equals(e.getActionCommand())) {
			// Create and set up the window.
			JFrame frame = new JFrame("join");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// Add contents to the window.
			frame.add(new Join());

			// Display the window.
			frame.pack();
			frame.setVisible(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("actionPerformed(ActionEvent) - end");
		}
	}

	public void go() {
		if (logger.isDebugEnabled()) {
			logger.debug("main(String[]) - start");
		}

		logger.info("launching login window");

		Startup launch = new Startup(messenger);
		launch.setVisible(true);

		if (logger.isDebugEnabled()) {
			logger.debug("main(String[]) - end");
		}
	}

	public GUI getGUI() {
		return gui;
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Messenger messenger = new Messenger();
		Startup startUp = new Startup(messenger);
		startUp.go();
		frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().add(startUp, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		TrayIconMarker trayIcon = new TrayIconMarker(messenger, startUp);
	}
}

class TrayIconMarker implements MouseListener {
	private Startup startup;
	private Messenger messenger;
	private TrayIcon trayIcon;
	private TrayMenu menu;

	TrayIconMarker(Messenger messenger, Startup startup) {
		this.messenger = messenger;
		this.startup = startup;
		menu = new TrayMenu();
		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\tray\\tray.png"));
		TrayIcon trayIcon = new TrayIcon(img);
		trayIcon.addMouseListener(this);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("Problem loading Tray icon");
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			menu.maybeShowPopup(e);
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			menu.maybeShowPopup(e);
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!messenger.isLoggedIn()) {
				System.out.println("peppy");
				startup.setVisible(true);
			} else {
				System.out.println("poppy");
				GUI gui = startup.getGUI();
				gui.setVisible(true);
			}
		}

	}

	class TrayMenu extends JPopupMenu implements ActionListener {

		private JPopupMenu popupMenu;
		private JMenuItem menuItem;
		private Contact contact;

		TrayMenu() {
			popupMenu = new JPopupMenu();
			menuItem = new JMenuItem("Exit");
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
		}

		public void actionPerformed(ActionEvent e) {
			if ("Exit".equals(e.getActionCommand())) {
				if (messenger.isLoggedIn())
				{
					messenger.Logout(messenger.getAccount().getScreenName(),
							messenger.getAccount().getPassword());
					messenger.endUpdaterThread();
				}
				System.exit(0);
			}
		}

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
