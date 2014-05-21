package com.client.video;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.common.Contact;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * AVReceive2 to receive RTP transmission using the new RTP API.
 */
public class Receiver implements ReceiveStreamListener, SessionListener,
		ControllerListener {
	Contact contact = null;
	RTPManager mgrs[] = null;
	Vector playerWindows = null;
	int port = 0;
	boolean finished = false;

	boolean dataReceived = false;
	Object dataSync = new Object();

	public Receiver(Contact contact, String port) {
		this.contact = contact;
		this.port = Integer.parseInt(port);
	}

	protected boolean initialize() {

		try {
			InetAddress ipAddr;
			SessionAddress localAddr = new SessionAddress();
			SessionAddress destAddr;

			mgrs = new RTPManager[1];
			playerWindows = new Vector();

			SessionLabel session;

			// Parse the session addresses.
			try {
				session = new SessionLabel(contact.getIP() + "/" + port);
			} catch (IllegalArgumentException e) {
				System.err
						.println("Failed to parse the session address given: "
								+ contact.getIP());
				return false;
			}

			System.err.println("  - Open RTP session for: addr: "
					+ session.addr + " port: " + session.port + " ttl: "
					+ session.ttl);

			mgrs[0] = (RTPManager) RTPManager.newInstance();
			mgrs[0].addSessionListener(this);
			mgrs[0].addReceiveStreamListener(this);

			ipAddr = InetAddress.getByName(session.addr);

			if (ipAddr.isMulticastAddress()) {
				// local and remote address pairs are identical:
				localAddr = new SessionAddress(ipAddr, session.port,
						session.ttl);
				destAddr = new SessionAddress(ipAddr, session.port, session.ttl);
			} else {
				localAddr = new SessionAddress(InetAddress.getLocalHost(),
						session.port);
				destAddr = new SessionAddress(ipAddr, session.port);
			}

			mgrs[0].initialize(localAddr);

			// You can try out some other buffer size to see
			// if you can get better smoothness.
			BufferControl bc = (BufferControl) mgrs[0]
					.getControl("javax.media.control.BufferControl");
			if (bc != null)
				bc.setBufferLength(350);

			mgrs[0].addTarget(destAddr);

		} catch (Exception e) {
			System.err.println("Cannot create the RTP Session: "
					+ e.getMessage());
			return false;
		}

		// Wait for data to arrive before moving on.

		long then = System.currentTimeMillis();
		long waitingPeriod = 30000; // wait for a maximum of 30 secs.

		try {
			synchronized (dataSync) {
				while (!dataReceived
						&& System.currentTimeMillis() - then < waitingPeriod) {
					if (!dataReceived)
						System.err
								.println("  - Waiting for RTP data to arrive...");
					dataSync.wait(1000);
				}
			}
		} catch (Exception e) {
		}

		if (!dataReceived) {
			System.err.println("No RTP data was received.");
			close();
			return false;
		}

		return true;
	}

	public boolean isDone() {
		return playerWindows.size() == 0;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void setFinished() {
		finished = true;
	}

	/**
	 * Close the players and the session managers.
	 */
	protected void close() {

		for (int i = 0; i < playerWindows.size(); i++) {
			try {
				((PlayerWindow) playerWindows.elementAt(i)).close();
			} catch (Exception e) {
			}
		}

		playerWindows.removeAllElements();

		// close the RTP session.
		for (int i = 0; i < mgrs.length; i++) {
			if (mgrs[i] != null) {
				mgrs[i].removeTargets("Closing session from AVReceive2");
				mgrs[i].dispose();
				mgrs[i] = null;
			}
		}
	}

	PlayerWindow find(Player p) {
		for (int i = 0; i < playerWindows.size(); i++) {
			PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
			if (pw.player == p)
				return pw;
		}
		return null;
	}

	PlayerWindow find(ReceiveStream strm) {
		for (int i = 0; i < playerWindows.size(); i++) {
			PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
			if (pw.stream == strm)
				return pw;
		}
		return null;
	}

	/**
	 * SessionListener.
	 */
	public synchronized void update(SessionEvent evt) {
		if (evt instanceof NewParticipantEvent) {
			Participant p = ((NewParticipantEvent) evt).getParticipant();
			System.err.println("  - A new participant had just joined: "
					+ p.getCNAME());
		}
	}

	/**
	 * ReceiveStreamListener
	 */
	public synchronized void update(ReceiveStreamEvent evt) {

		RTPManager mgr = (RTPManager) evt.getSource();
		Participant participant = evt.getParticipant(); // could be null.
		ReceiveStream stream = evt.getReceiveStream(); // could be null.

		if (evt instanceof RemotePayloadChangeEvent) {

			System.err.println("  - Received an RTP PayloadChangeEvent.");
			System.err.println("Sorry, cannot handle payload change.");

		}

		else if (evt instanceof NewReceiveStreamEvent) {

			try {
				stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
				DataSource ds = stream.getDataSource();

				// Find out the formats.
				RTPControl ctl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl");
				if (ctl != null) {
					System.err.println("  - Recevied new RTP stream: "
							+ ctl.getFormat());
				} else
					System.err.println("  - Recevied new RTP stream");

				if (participant == null)
					System.err
							.println("      The sender of this stream had yet to be identified.");
				else {
					System.err.println("      The stream comes from: "
							+ participant.getCNAME());
				}

				// create a player by passing datasource to the Media Manager
				Player p = javax.media.Manager.createPlayer(ds);
				if (p == null)
					return;

				p.addControllerListener(this);
				p.realize();
				PlayerWindow pw = new PlayerWindow(p, stream);
				playerWindows.addElement(pw);

				// Notify intialize() that a new stream had arrived.
				synchronized (dataSync) {
					dataReceived = true;
					dataSync.notifyAll();
				}

			} catch (Exception e) {
				System.err.println("NewReceiveStreamEvent exception "
						+ e.getMessage());
				return;
			}

		}

		else if (evt instanceof StreamMappedEvent) {

			if (stream != null && stream.getDataSource() != null) {
				DataSource ds = stream.getDataSource();
				// Find out the formats.
				RTPControl ctl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl");
				System.err.println("  - The previously unidentified stream ");
				if (ctl != null)
					System.err.println("      " + ctl.getFormat());
				System.err.println("      had now been identified as sent by: "
						+ participant.getCNAME());
			}
		}

		else if (evt instanceof ByeEvent) {

			System.err.println("  - Got \"bye\" from: "
					+ participant.getCNAME());
			PlayerWindow pw = find(stream);
			if (pw != null) {
				pw.close();
				playerWindows.removeElement(pw);
			}
		}

	}

	/**
	 * ControllerListener for the Players.
	 */
	public synchronized void controllerUpdate(ControllerEvent ce) {

		Player p = (Player) ce.getSourceController();

		if (p == null)
			return;

		// Get this when the internal players are realized.
		if (ce instanceof RealizeCompleteEvent) {
			PlayerWindow pw = find(p);
			if (pw == null) {
				// Some strange happened.
				System.err.println("Internal error!");
			}
			pw.initialize();
			pw.setVisible(true);
			p.start();
		}

		if (ce instanceof ControllerErrorEvent) {
			p.removeControllerListener(this);
			PlayerWindow pw = find(p);
			if (pw != null) {
				pw.close();
				playerWindows.removeElement(pw);
			}
			System.err.println("AVReceive2 internal error: " + ce);
		}

	}

	/**
	 * A utility class to parse the session addresses.
	 */
	class SessionLabel {

		public String addr = null;
		public int port;
		public int ttl = 1;

		SessionLabel(String session) throws IllegalArgumentException {

			int off;
			String portStr = null, ttlStr = null;

			if (session != null && session.length() > 0) {
				while (session.length() > 1 && session.charAt(0) == '/')
					session = session.substring(1);

				// Now see if there's a addr specified.
				off = session.indexOf('/');
				if (off == -1) {
					if (!session.equals(""))
						addr = session;
				} else {
					addr = session.substring(0, off);
					session = session.substring(off + 1);
					// Now see if there's a port specified
					off = session.indexOf('/');
					if (off == -1) {
						if (!session.equals(""))
							portStr = session;
					} else {
						portStr = session.substring(0, off);
						session = session.substring(off + 1);
						// Now see if there's a ttl specified
						off = session.indexOf('/');
						if (off == -1) {
							if (!session.equals(""))
								ttlStr = session;
						} else {
							ttlStr = session.substring(0, off);
						}
					}
				}
			}

			if (addr == null)
				throw new IllegalArgumentException();

			if (portStr != null) {
				try {
					Integer integer = Integer.valueOf(portStr);
					if (integer != null)
						port = integer.intValue();
				} catch (Throwable t) {
					throw new IllegalArgumentException();
				}
			} else
				throw new IllegalArgumentException();

			if (ttlStr != null) {
				try {
					Integer integer = Integer.valueOf(ttlStr);
					if (integer != null)
						ttl = integer.intValue();
				} catch (Throwable t) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * GUI classes for the Player.
	 */
	class PlayerWindow extends JFrame implements WindowListener {

		Player player;
		ReceiveStream stream;

		PlayerWindow(Player p, ReceiveStream strm) {
			player = p;
			stream = strm;
			addWindowListener(this);
		}

		public void initialize() {
			add(new PlayerPanel(player));
		}

		public void windowClosing(WindowEvent e) {
			close();
		}

		public void close() {
			player.close();
			setVisible(false);
			dispose();
			finished = true;
		}

		public void addNotify() {
			super.addNotify();
			pack();
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}
		
		/**
		 * GUI classes for the Player.
		 */
		class PlayerPanel extends Panel implements ActionListener{
			private JButton capture;
			private Buffer BUF;
			private Image img;
			private VideoFormat VF;
			private BufferToImage BtoI;

			Component vc, cc;

			PlayerPanel(Player p) {
				setLayout(new BorderLayout());
				if ((vc = p.getVisualComponent()) != null)
					add("North", vc);
				if ((cc = p.getControlPanelComponent()) != null)
					add("Center", cc);
				capture = new JButton("Screenshot");
				capture.addActionListener(this);
				add("South", capture);
			}

			public Dimension getPreferredSize() {
				int w = 0, h = 0;
				if (vc != null) {
					Dimension size = vc.getPreferredSize();
					w = size.width;
					h = size.height;
				}
				if (cc != null) {
					Dimension size = cc.getPreferredSize();
					if (w == 0)
						w = size.width;
					h += size.height;
				}
				if (w < 160)
					w = 160;
				return new Dimension(w, h);
			}
			
			public void action() { // your action handler code.....
				// Grab a frame
				FrameGrabbingControl fgc = (FrameGrabbingControl) player
						.getControl("javax.media.control.FrameGrabbingControl");
				BUF = fgc.grabFrame();

				// Convert it to an image
				BtoI = new BufferToImage((VideoFormat) BUF.getFormat());
				img = BtoI.createImage(BUF);

				// save image
				saveJPG(img, "d:\\test.jpg");

			}

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		}

		public void saveJPG(Image img, String s) {
			BufferedImage bi = new BufferedImage(img.getWidth(null), img
					.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = bi.createGraphics();
			g2.drawImage(img, null, null);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(s);
			} catch (java.io.FileNotFoundException io) {
				System.out.println("File Not Found");
			}

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
			param.setQuality(0.5f, false);
			encoder.setJPEGEncodeParam(param);

			try {
				encoder.encode(bi);
				out.close();
			} catch (java.io.IOException io) {
				System.out.println("IOException");
			}
		}
	}

}