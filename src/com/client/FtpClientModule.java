/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.common.Contact;

/**
 * 
 * @author Kev
 */
public class FtpClientModule {

	private FTPClient client;

	public FtpClientModule() {
		client = new FTPClient();
	}

	public void Connect(Contact contact) {
		System.out.println(contact.getIP() + contact.getScreenName()
				+ contact.getFtpPassword());
		if (contact.getFtpPassword() == null)
		{
			contact.setFtpPassword("na");
		}
		try {
			client.connect(contact.getIP());
			client.login(contact.getScreenName(), contact.getFtpPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			client.disconnect(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[][] listNames() {
		String returnList[][] = null;
		try {
			String[] list = client.listNames();
			returnList = new String[list.length][1];
			for (int i = 0; i < list.length; i++) {
				returnList[i][0] = list[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnList;
	}

	public FTPFile[] list() {
		FTPFile[] list = null;
		try {
			list = client.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getCurrentDirectory() {
		String returnList = null;
		try {
			returnList = client.currentDirectory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnList;
	}

	public int getSize(String path) {
		int i = 0;
		try {
			return (int) client.fileSize(path);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPIllegalReplyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

	public String[][] ChangeDirectory(String dir) {
		String returnList[][] = null;
		try {
			client.changeDirectory(dir);
			String[] list = client.listNames();
			list = client.listNames();
			returnList = new String[list.length][1];
			for (int i = 0; i < list.length; i++) {
				returnList[i][0] = list[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnList;
	}

	public String[][] ChangeDirectoryUp() {
		String returnList[][] = null;
		try {
			client.changeDirectoryUp();
			String[] list = client.listNames();
			list = client.listNames();
			returnList = new String[list.length][1];
			for (int i = 0; i < list.length; i++) {
				returnList[i][0] = list[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnList;
	}

	public void download(String type, String name,
			TransferListener transferListener) throws IllegalStateException,
			FileNotFoundException, IOException, FTPIllegalReplyException,
			FTPException, FTPDataTransferException, FTPAbortedException {
		client.download(name, new java.io.File(System.getenv("ULYSSES_HOME")
				+ "\\Library\\" + type + "\\" + name), transferListener);
	}

	public void download(String type, String name,
			TransferListener transferListener, String path)
			throws IllegalStateException, FileNotFoundException, IOException,
			FTPIllegalReplyException, FTPException, FTPDataTransferException,
			FTPAbortedException {
		client.download(name, new java.io.File(System.getenv("ULYSSES_HOME")
				+ "\\Library\\" + path + "\\" + name), transferListener);
	}

	public void downloadAv(String name) throws IllegalStateException,
			FileNotFoundException, IOException, FTPIllegalReplyException,
			FTPException, FTPDataTransferException, FTPAbortedException {
		client.download(name, new java.io.File(System.getenv("ULYSSES_HOME")
				+ "/blah/" + name));
	}

	public void cancelDownload() {
		try {
			client.abortCurrentDataTransfer(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
