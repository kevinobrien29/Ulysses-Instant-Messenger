package com.client;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TransferListener implements FTPDataTransferListener {
	private JProgressBar progressBar;
	private JTable transferTable;
	private int i = 0;
	private int size = 0;
	private int loc = 0;
	private int x = 0;
	private int y = 0;
	TransferListener(JProgressBar progressBar, JTable transferTable, int size)
	{
		this.progressBar = progressBar;
		this.transferTable = transferTable;
		this.size = size;
		
	}

	public void started() {
		// Transfer started
		progressBar.setValue(0);
		progressBar.setMaximum(size);
		progressBar.setStringPainted(true);
		progressBar.updateUI();
		transferTable.updateUI();
	}
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void transferred(int length) {
		loc = loc + length;
		x = (int)(100*(double)loc/(double)size);
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	progressBar.setValue(x);
        		progressBar.setString(x + "%");
        		progressBar.setStringPainted(true);
        		progressBar.updateUI();
        		if (x != y)
        		{
        			transferTable.updateUI();
        		}
            }
          });
		
		y = x;
	}

	public void completed() {
		// Transfer completed
		progressBar.setValue(100);
		progressBar.setString("Completed");
		progressBar.updateUI();
		transferTable.updateUI();
	}

	public void aborted() {
		// Transfer aborted
		progressBar.setValue(0);
		progressBar.setString("aborted");
		progressBar.updateUI();
		transferTable.updateUI();
	}

	public void failed() {
		// Transfer failed
		progressBar.setValue(0);
		progressBar.setString("failed");
		progressBar.updateUI();
		transferTable.updateUI();
	}
}