package com.client;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.common.Contact;

public class MediaModule extends Thread {
	private FtpClientModule ftpClientModule;
	private ArrayList<Download> downloads = new ArrayList<Download>();
	private TaskList taskList = new TaskList();
	private Contact contact;

	public MediaModule(Contact contact) {
		this.contact = contact;
		this.ftpClientModule = new FtpClientModule();
	}

	public void connect() {
		Task task = new Task("connect");
		task.addParameter(contact);
		taskList.addTask(task);
	}

	public void changeCategory(String category, JTable audioTable,
			AudioTableModel audioTableModel) {
		Task task = new Task("changeCategory");
		task.addParameter(category);
		task.addParameter(audioTable);
		task.addParameter(audioTableModel);
		taskList.addTask(task);
	}

	public void ChangeDirectory(String dir, JTable audioTable,
			AudioTableModel audioTableModel) {
		Task task = new Task("ChangeDirectory");
		task.addParameter(dir);
		task.addParameter(audioTable);
		task.addParameter(audioTableModel);
		taskList.addTask(task);
	}

	public FTPFile[] dir() {
		return ftpClientModule.list();
	}

	public void clear() {
		for (int i = 0; i < downloads.size(); i++) {
			if (downloads.get(i).isFinished()) {
				downloads.get(i).clear();
			}
		}
	}

	public String currentDirectory() {
		return ftpClientModule.getCurrentDirectory();
	}

	public void cancelDownload(UUID code) {
		for (int i = 0; i < downloads.size(); i++) {
			if (downloads.get(i).isFinished()) {
				downloads.get(i).cancel();
				i = downloads.size();
			}
		}
	}

	public FTPFile[] back() {
		ftpClientModule.ChangeDirectoryUp();
		return ftpClientModule.list();
	}

	public void addDownload(String path, String type, String name,
			JTable transferTable) {
		FTPFile[] list = ftpClientModule.list();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().equals(name)) {
				if (list[i].getType() == 1) {
					addDownloadFolder(path, type, name, transferTable);
				} else if (list[i].getType() == 0) {
					addDownloadOther(path, type, name, transferTable);
				}
				break;
			}
		}

	}

	public void downLoadAvs(String path) {
		FtpClientModule ftp = new FtpClientModule();
		ftp.Connect(contact);
		ftp.ChangeDirectory(path);
		System.out.println(ftp.getCurrentDirectory());
		ftp.ChangeDirectory("thumbs");
		FTPFile[] list = ftp.list();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getType() == 0) {
				try {
					ftp.downloadAv(list[i].getName());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
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
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ftp.disconnect();
	}

	public void addDownloadFolder(String path, String type, String name,
			JTable transferTable) {
		FolderDownload download = new FolderDownload(path, type, name, contact,
				transferTable);
		download.start();
	}

	public void addDownloadOther(String path, String type, String name,
			JTable transferTable) {
		Download download = new Download(path, type, name, contact,
				transferTable);
		download.start();
		downloads.add(download);
	}

	public void run() {
		while (true) {
			if (taskList.available() > 0) {
				Task task = taskList.getTask();
				String operation = task.operation();
				Object[] parameters = task.getParameters();
				if (operation.equals("connect")) {
					ftpClientModule.Connect((Contact) parameters[0]);
				} else if (operation.equals("changeCategory")) {
					ftpClientModule.ChangeDirectory("/");
					ftpClientModule.ChangeDirectory("/");
					ftpClientModule.ChangeDirectory((String) parameters[0]);
					AudioTableModel audioTableModel = (AudioTableModel) parameters[2];
					audioTableModel.update(dir());
					JTable audioTable = (JTable) parameters[1];
					audioTable.updateUI();

				} else if (operation.equals("ChangeDirectory")) {
					ftpClientModule.ChangeDirectory((String) parameters[0]);
					AudioTableModel audioTableModel = (AudioTableModel) parameters[2];
					audioTableModel.update(dir());
					JTable audioTable = (JTable) parameters[1];
					audioTable.updateUI();
				}
			}
		}
	}

	private class TaskList {
		ArrayList<Task> tasks = new ArrayList<Task>();

		TaskList() {
		}

		public void addTask(Task task) {
			tasks.add(task);
		}

		public Task getTask() {
			return tasks.remove(0);
		}

		public int available() {
			return tasks.size();
		}

		public void clear() {
			tasks.clear();
		}
	}

	private class Task {
		ArrayList<Object> parameters = new ArrayList<Object>();
		String operation;

		Task(String operation) {
			this.operation = operation;
		}

		public void addParameter(Object parameter) {
			parameters.add(parameter);
		}

		public Object[] getParameters() {
			Object[] params = new Object[parameters.size()];
			for (int i = 0; i < parameters.size(); i++) {
				params[i] = parameters.get(i);
			}
			return params;
		}

		public String operation() {
			return operation;
		}
	}

	public class Download extends Thread {
		UUID code;
		String path;
		String type;
		String name;
		FtpClientModule ftpClientModule;
		JProgressBar progressBar;
		Boolean running;
		JTable transferTable;
		TransferTableModel transferTableModel;
		Boolean finished = false;

		Download(String path, String type, String name, Contact contact,
				JTable transferTable) {
			code = UUID.randomUUID();
			this.transferTable = transferTable;
			this.transferTableModel = (TransferTableModel) transferTable
					.getModel();
			this.path = path;
			this.type = type;
			this.name = name;
			this.progressBar = createBar(0, "0%");
			this.ftpClientModule = new FtpClientModule();
			this.ftpClientModule.Connect(contact);
			code = UUID.randomUUID();
		}

		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					transferTableModel.add(code, name, progressBar);
					transferTable.updateUI();
				}
			});
			ftpClientModule.ChangeDirectory(path);
			TransferListener transferListener = new TransferListener(
					progressBar, transferTable, ftpClientModule.getSize(path
							+ "//" + name));
			try {
				ftpClientModule.download(type, name, transferListener);

				finished = true;
				ftpClientModule.disconnect();
				progressBar.setValue(0);
				progressBar.setString("Finished");
				progressBar.updateUI();
				transferTable.updateUI();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
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
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				progressBar.setValue(0);
				progressBar.setString("aborted");
				progressBar.updateUI();
				transferTable.updateUI();
				finished = true;
			}
		}

		public UUID getCode() {
			return code;
		}

		public void clear() {
			transferTableModel.removeRow(code);
			progressBar.updateUI();
			transferTable.updateUI();
		}

		public Boolean isFinished() {
			return finished;
		}

		public void cancel() {
			ftpClientModule.cancelDownload();
			ftpClientModule.disconnect();
			progressBar.setValue(0);
			progressBar.setString("cancelled");
			progressBar.updateUI();
			transferTable.updateUI();
			finished = true;
		}

		public JProgressBar createBar(int percentDone, String text) {
			JProgressBar progressBar = new JProgressBar(0, 100);
			progressBar.setStringPainted(true);
			progressBar.setValue(percentDone);
			progressBar.setString(text);
			return progressBar;
		}
	}

	public class FolderDownload extends Thread {
		UUID code;
		String path;
		String type;
		String name;
		FtpClientModule ftpClientModule;
		JProgressBar progressBar;
		Boolean running;
		JTable transferTable;
		TransferTableModel transferTableModel;
		Boolean finished = false;
		TransferListener transferListener;

		FolderDownload(String path, String type, String name, Contact contact,
				JTable transferTable) {
			code = UUID.randomUUID();
			this.transferTable = transferTable;
			this.transferTableModel = (TransferTableModel) transferTable
					.getModel();
			this.path = path;
			this.type = type;
			this.name = name;
			this.progressBar = createBar(0, "0%");
			this.ftpClientModule = new FtpClientModule();
			this.ftpClientModule.Connect(contact);
			code = UUID.randomUUID();
		}

		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					transferTableModel.add(code, name, progressBar);
					transferTable.updateUI();
				}
			});
			ftpClientModule.ChangeDirectory(path);
			ftpClientModule.ChangeDirectory(name);
			System.out.println(ftpClientModule.getCurrentDirectory());
			createFolder(System.getenv("ULYSSES_HOME") + "\\Library"
					+ ftpClientModule.getCurrentDirectory());
			try {
				FTPFile[] list = ftpClientModule.list();
				for (int i = 0; i < list.length; i++) {
					if (list[i].getType() == 1) {
						System.out.println(list[i].getName() + " Folder");
						downloadFolder(list[i].getName(), type);
					} else if (list[i].getType() == 0) {
						transferListener = new TransferListener(progressBar,
								transferTable, ftpClientModule
										.getSize(ftpClientModule
												.getCurrentDirectory()
												+ "//" + list[i].getName()));
						ftpClientModule.download(type, list[i].getName(),
								transferListener, ftpClientModule
										.getCurrentDirectory());
						System.out.println(list[i].getName() + " other");
					}
				}

				finished = true;
				ftpClientModule.disconnect();
				progressBar.setValue(0);
				progressBar.setString("Finished");
				progressBar.updateUI();
				transferTable.updateUI();
			} catch (FileNotFoundException e) {
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
			} catch (FTPDataTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FTPAbortedException e) {
				progressBar.setValue(0);
				progressBar.setString("aborted");
				progressBar.updateUI();
				transferTable.updateUI();
				finished = true;
			}
		}

		public void downloadFolder(String name, String type) {
			ftpClientModule.ChangeDirectory(name);
			createFolder(System.getenv("ULYSSES_HOME") + "//Library"
					+ ftpClientModule.getCurrentDirectory());
			FTPFile[] list = ftpClientModule.list();
			for (int i = 0; i < list.length; i++) {
				if (list[i].getType() == 0) {
					try {
						ftpClientModule.download(type, list[i].getName(),
								transferListener, ftpClientModule
										.getCurrentDirectory());
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
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
					} catch (FTPDataTransferException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FTPAbortedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (list[i].getType() == 1) {
					System.out.println(list[i].getName());
					addDownloadFolder(ftpClientModule.getCurrentDirectory(),
							type, list[i].getName(), transferTable);
				}
			}
			ftpClientModule.ChangeDirectoryUp();
		}

		public UUID getCode() {
			return code;
		}

		public void clear() {
			transferTableModel.removeRow(code);
			progressBar.updateUI();
			transferTable.updateUI();
		}

		public Boolean isFinished() {
			return finished;
		}

		public void cancel() {
			ftpClientModule.cancelDownload();
			ftpClientModule.disconnect();
			progressBar.setValue(0);
			progressBar.setString("cancelled");
			progressBar.updateUI();
			transferTable.updateUI();
			finished = true;
		}

		public JProgressBar createBar(int percentDone, String text) {
			JProgressBar progressBar = new JProgressBar(0, 100);
			progressBar.setStringPainted(true);
			progressBar.setValue(percentDone);
			progressBar.setString(text);
			return progressBar;
		}

		private Boolean createFolder(String path) {
			File folder = null;
			try {
				folder = new File(path);
				folder.mkdirs();
				System.out.println(path + folder.exists());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;

		}
	}
}
