package com.client;

import it.sauronsoftware.ftp4j.FTPFile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.common.Contact;

public class MediaView extends JFrame {

	private String[] titles = { "Audio", "Video", "Images" };
	private JTabbedPane mediaPane;
	private JTabbedPane topTabbedPane;
	private JPanel transfersPane;
	private TransferView transferView;
	private String host;

	private MediaModule mediaModule;
	private final AudioView audioView;

	public MediaView(Contact contact) {

		topTabbedPane = new JTabbedPane();
		mediaPane = new JTabbedPane();
		transfersPane = new JPanel();
		mediaModule = new MediaModule(contact);
		mediaModule.start();
		transferView = new TransferView(mediaModule);

		audioView = new AudioView(transferView, mediaModule);

		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\audio.png"));
		ImageIcon icon = new ImageIcon(img);

		Image img2 = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\video.png"));
		ImageIcon icon2 = new ImageIcon(img2);

		Image img3 = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\images.png"));
		ImageIcon icon3 = new ImageIcon(img3);

		mediaPane.addTab(titles[0], icon, audioView);

		final VideoView videoView = new VideoView(transferView, mediaModule);
		mediaPane.addTab(titles[1], icon2, videoView);

		final ImageView imageView = new ImageView(transferView, mediaModule);
		mediaPane.addTab(titles[2], icon3, imageView);

		mediaPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (mediaPane.getSelectedIndex() == 0) {
					audioView.updateMedia();
				} else if (mediaPane.getSelectedIndex() == 1) {
					videoView.updateMedia();
				} else if (mediaPane.getSelectedIndex() == 2) {
					imageView.updateMedia();
				}
			}
		});

		transfersPane.add(transferView);

		topTabbedPane.addTab("Media", mediaPane);
		topTabbedPane.addTab("Transfers", transfersPane);
		getContentPane().add(topTabbedPane);
	}

	public void connect() {
		mediaModule.connect();
		audioView.updateMedia();
	}
}

class AudioView extends JPanel implements ActionListener {

	private JTable audioTable;
	MediaModule mediaModule;
	AudioTableModel audioTableModel;
	TransferView transferView;
	String host;

	public AudioView(TransferView transferView, final MediaModule mediaModule) {
		this.mediaModule = mediaModule;
		this.transferView = transferView;
		audioTableModel = new AudioTableModel();
		audioTable = new JTable();
		audioTable.setModel(audioTableModel);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(audioTable);
		audioTable.getColumnModel().getColumn(0).setCellRenderer(
				new MyRenderer());

		add(scrollPane, BorderLayout.CENTER);
		add(createColumnSelectionPanel(audioTable.getColumnModel()),
				BorderLayout.WEST);

		audioTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					mediaModule.ChangeDirectory(((MediaItem) audioTableModel
							.getValueAt(row, 1)).toString(), audioTable, audioTableModel);
				}
			}
		});
	}

	public void updateMedia() {
		mediaModule.changeCategory("Audio", audioTable, audioTableModel);
	}

	/**
	 * createColumnSelectionPanel() - Creates a panel containing a check box
	 * corresponding to each column in the table model. When a check box is
	 * selected the column appears in the table. When a check box is deselected
	 * the column is removed.
	 * 
	 * @param columnModel
	 * @return
	 */
	private JComponent createColumnSelectionPanel(TableColumnModel columnModel) {
		JPanel selectionPanel = new JPanel();
		selectionPanel
				.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\back.png"));
		ImageIcon icon = new ImageIcon(img);
		JButton back = new JButton(icon);
		back.addActionListener(this);
		back.setActionCommand("back");
		back.setSelected(true);
		selectionPanel.add(back);

		img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\download.png"));
		icon = new ImageIcon(img);
		JButton download = new JButton(icon);
		download.addActionListener(this);
		download.setActionCommand("download");
		download.setSelected(true);
		selectionPanel.add(download);
		return selectionPanel;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("back")
				&& !(mediaModule.currentDirectory().equals("/Audio")
						|| mediaModule.currentDirectory().equals("/Video") || mediaModule
						.currentDirectory().equals("/Images"))) {
			mediaModule.back();
			audioTableModel.update(mediaModule.dir());
			audioTable.updateUI();
		} else if (e.getActionCommand().equals("download")) {
			int row = audioTable.getSelectedRow();
			int column = audioTable.getSelectedColumn();
			transferView.addDownload(mediaModule.currentDirectory(), "Audio",
					((MediaItem)audioTableModel.getValueAt(row, column)).getName());
		}
	}

	class MyRenderer extends DefaultTableCellRenderer {

		/*
		 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object,
		 * boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (((MediaItem)audioTableModel.getValueAt(row, 1)).getType() == 1)
			{
				Image img = Toolkit.getDefaultToolkit().getImage(
						Thread.currentThread().getContextClassLoader().getResource(
								"\\icons\\MediaView\\audioFolder.png"));
				ImageIcon icon = new ImageIcon(img);
	
				setIcon(icon);
	
				int preferredWidth = (int) 50;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(50);
			}
			else
			{
				Image img = Toolkit.getDefaultToolkit().getImage(
						Thread.currentThread().getContextClassLoader().getResource(
								"\\icons\\MediaView\\audioItem.png"));
				ImageIcon icon = new ImageIcon(img);
	
				setIcon(icon);
	
				int preferredWidth = (int) 50;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(50);
			}
			return this;
		}
	}
}

class VideoView extends JPanel implements ActionListener {

	private JTable audioTable;
	MediaModule mediaModule;
	AudioTableModel audioTableModel;
	TransferView transferView;

	public VideoView(TransferView transferView, final MediaModule mediaModule) {
		this.transferView = transferView;
		this.mediaModule = mediaModule;
		audioTableModel = new AudioTableModel();
		audioTable = new JTable();
		audioTable.setModel(audioTableModel);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(audioTable);
		audioTable.getColumnModel().getColumn(0).setCellRenderer(
				new MyRenderer());

		add(scrollPane, BorderLayout.CENTER);
		add(createColumnSelectionPanel(audioTable.getColumnModel()),
				BorderLayout.WEST);

		audioTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					mediaModule.ChangeDirectory((String) audioTableModel
							.getValueAt(row, 1), audioTable,
							audioTableModel);
				}
			}
		});
	}

	public void updateMedia() {
		mediaModule.changeCategory("Video", audioTable, audioTableModel);
	}

	/**
	 * createColumnSelectionPanel() - Creates a panel containing a check box
	 * corresponding to each column in the table model. When a check box is
	 * selected the column appears in the table. When a check box is deselected
	 * the column is removed.
	 * 
	 * @param columnModel
	 * @return
	 */
	private JComponent createColumnSelectionPanel(TableColumnModel columnModel) {
		JPanel selectionPanel = new JPanel();
		selectionPanel
				.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\back.png"));
		ImageIcon icon = new ImageIcon(img);
		JButton back = new JButton(icon);
		back.addActionListener(this);
		back.setActionCommand("back");
		back.setSelected(true);
		selectionPanel.add(back);

		img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\download.png"));
		icon = new ImageIcon(img);
		JButton download = new JButton(icon);
		download.addActionListener(this);
		download.setActionCommand("download");
		download.setSelected(true);
		selectionPanel.add(download);
		return selectionPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("back")
				&& !(mediaModule.currentDirectory().equals("/Audio")
						|| mediaModule.currentDirectory().equals("/Video") || mediaModule
						.currentDirectory().equals("/Images"))) {
			mediaModule.back();
			audioTableModel.update(mediaModule.dir());
			audioTable.updateUI();
		} else if (e.getActionCommand().equals("download")) {
			int row = audioTable.getSelectedRow();
			int column = audioTable.getSelectedColumn();
			transferView.addDownload(mediaModule.currentDirectory(), "Video",
					((MediaItem)audioTableModel.getValueAt(row, 1)).getName());
		}
	}

	class MyRenderer extends DefaultTableCellRenderer {

		/*
		 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object,
		 * boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (((MediaItem)audioTableModel.getValueAt(row, 1)).getType() == 0)
			{
				Image img = null;
				if (new File(System.getenv("ULYSSES_HOME") + "/blah/"
						+ audioTableModel.getValueAt(row, 1)).exists())
				{
					img = Toolkit.getDefaultToolkit().getImage(
							System.getenv("ULYSSES_HOME") + "/blah/"
									+ audioTableModel.getValueAt(row, 1));
				}
				else
				{
					img = Toolkit.getDefaultToolkit().getImage(
							Thread.currentThread().getContextClassLoader().getResource(
									"\\icons\\MediaView\\audioItem.png"));
				}
				ImageIcon icon = new ImageIcon(img);
				setIcon(icon);
	
				int preferredHeight = (int) 100;
				int preferredWidth = (int) 100;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(100);
			}
			else
			{
				Image img = Toolkit.getDefaultToolkit().getImage(
						Thread.currentThread().getContextClassLoader().getResource(
								"\\icons\\MediaView\\audioFolder.png"));
				ImageIcon icon = new ImageIcon(img);
				setIcon(icon);
	
				int preferredHeight = (int) 100;
				int preferredWidth = (int) 100;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(100);
			}
			return this;
		}
	}
}

class ImageView extends JPanel implements ActionListener {

	private JTable audioTable;
	MediaModule mediaModule;
	AudioTableModel audioTableModel;
	TransferView transferView;

	public ImageView(TransferView transferView, final MediaModule mediaModule) {
		this.transferView = transferView;
		this.mediaModule = mediaModule;
		audioTableModel = new AudioTableModel();
		audioTable = new JTable();
		audioTable.setModel(audioTableModel);
		audioTable.getColumnModel().getColumn(0).setCellRenderer(
				new MyRenderer());
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(audioTable);

		add(scrollPane, BorderLayout.CENTER);
		add(createColumnSelectionPanel(audioTable.getColumnModel()),
				BorderLayout.WEST);

		audioTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					mediaModule.ChangeDirectory((String) audioTableModel
							.getValueAt(row, 0), audioTable, audioTableModel);
					ImageUpdater imageUpdater = new ImageUpdater(
							audioTableModel, mediaModule.currentDirectory());
					// imageUpdater.start();
				}
			}
		});
	}

	public void updateMedia() {
		mediaModule.changeCategory("Images", audioTable, audioTableModel);
		ImageUpdater imageUpdater = new ImageUpdater(audioTableModel,
				mediaModule.currentDirectory());
		// imageUpdater.start();
	}

	/**
	 * createColumnSelectionPanel() - Creates a panel containing a check box
	 * corresponding to each column in the table model. When a check box is
	 * selected the column appears in the table. When a check box is deselected
	 * the column is removed.
	 * 
	 * @param columnModel
	 * @return
	 */
	private JComponent createColumnSelectionPanel(TableColumnModel columnModel) {
		JPanel selectionPanel = new JPanel();
		selectionPanel
				.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
		Image img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\back.png"));
		ImageIcon icon = new ImageIcon(img);
		JButton back = new JButton(icon);
		back.addActionListener(this);
		back.setActionCommand("back");
		back.setSelected(true);
		selectionPanel.add(back);

		img = Toolkit.getDefaultToolkit().getImage(
				Thread.currentThread().getContextClassLoader().getResource(
						"\\icons\\MediaView\\download.png"));
		icon = new ImageIcon(img);
		JButton download = new JButton(icon);
		download.addActionListener(this);
		download.setActionCommand("download");
		download.setSelected(true);
		selectionPanel.add(download);
		return selectionPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("back")
				&& !(mediaModule.currentDirectory().equals("/Audio")
						|| mediaModule.currentDirectory().equals("/Video") || mediaModule
						.currentDirectory().equals("/Images"))) {
			mediaModule.back();
			audioTableModel.update(mediaModule.dir());
			audioTable.updateUI();
		} else if (e.getActionCommand().equals("download")) {
			int row = audioTable.getSelectedRow();
			int column = audioTable.getSelectedColumn();
			transferView.addDownload(mediaModule.currentDirectory(), "Images",
					((MediaItem)audioTableModel.getValueAt(row, column)).getName());
		}
	}

	class MyRenderer extends DefaultTableCellRenderer {

		/*
		 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object,
		 * boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (((MediaItem)audioTableModel.getValueAt(row, 1)).getType() == 0)
			{
				Image img = null;
				if (new File(System.getenv("ULYSSES_HOME") + "/blah/"
						+ audioTableModel.getValueAt(row, 1)).exists())
				{
					img = Toolkit.getDefaultToolkit().getImage(
							System.getenv("ULYSSES_HOME") + "/blah/"
									+ audioTableModel.getValueAt(row, 1));
				}
				else
				{
					img = Toolkit.getDefaultToolkit().getImage(
							Thread.currentThread().getContextClassLoader().getResource(
									"\\icons\\MediaView\\audioItem.png"));
				}
				ImageIcon icon = new ImageIcon(img);
				setIcon(icon);
	
				int preferredHeight = (int) 100;
				int preferredWidth = (int) 100;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(100);
			}
			else
			{
				Image img = Toolkit.getDefaultToolkit().getImage(
						Thread.currentThread().getContextClassLoader().getResource(
								"\\icons\\MediaView\\audioFolder.png"));
				ImageIcon icon = new ImageIcon(img);
				setIcon(icon);
	
				int preferredHeight = (int) 100;
				int preferredWidth = (int) 100;
	
				audioTable.getColumnModel().getColumn(column).setMaxWidth(
						preferredWidth);
				audioTable.getColumnModel().getColumn(column).setMinWidth(
						preferredWidth);
				audioTable.setRowHeight(100);
			}
			return this;
		}
	}

	class ImageUpdater extends Thread {
		AudioTableModel audioTableModel;
		String path;

		public ImageUpdater(AudioTableModel audioTableModel, String path) {
			this.audioTableModel = audioTableModel;
			this.path = path;
		}

		public void run() {
			// mediaModule.downLoadAvs(path);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<File> list = new ArrayList();
			File myDir = new File(System.getenv("ULYSSES_HOME") + "/blah");
			if (myDir.exists() && myDir.isDirectory()) {
				File[] contents = myDir.listFiles();
				for (int i = 0; i < contents.length; i++) {
					System.out.println("adding");
					list.add(contents[i]);
				}
			}
			audioTableModel.update(list);
		}
	}
}

class TransferView extends JPanel implements ActionListener {

	private JButton cancelButton;
	private JButton clearButton;
	private JTable transferTable;
	private TransferTableModel transferTableModel;
	MediaModule mediaModule;

	public void addDownload(String path, String type, String name) {
		mediaModule.addDownload(path, type, name, transferTable);
	}

	private class ProgRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return (JProgressBar) value;
		}

	}

	public TransferView(MediaModule mediaModule) {
		super(new BorderLayout());

		// Create the demo's UI.
		cancelButton = new JButton("cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		this.mediaModule = mediaModule;

		clearButton = new JButton("clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);

		transferTableModel = new TransferTableModel();
		transferTable = new JTable(transferTableModel);
		transferTable.getColumn("Progress").setCellRenderer(new ProgRenderer());

		add(cancelButton, BorderLayout.PAGE_START);
		add(clearButton, BorderLayout.CENTER);

		add(new JScrollPane(transferTable), BorderLayout.PAGE_END);

	}

	/**
	 * Invoked when the user presses the start button.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("cancel")) {
			int row = transferTable.getSelectedRow();
			if (row >= 0) {
				UUID code = (UUID) transferTable.getValueAt(row, 0);
				mediaModule.cancelDownload(code);
			}
		} else if (e.getActionCommand().equals("clear")) {
			mediaModule.clear();
		}
	}
}

class TransferTableModel extends AbstractTableModel {

	private String[] columnNames = { "Code", "Name", "Progress" };
	private Object[][] data = new Object[0][0];

	public void update(Object[][] value) {
		data = value;
	}

	public void add(UUID code, String name, JProgressBar progressBar) {
		Object[][] data2 = new Object[data.length + 1][3];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < 3; j++) {
				data2[i][j] = data[i][j];
			}
		}
		data2[data.length][0] = code;
		data2[data.length][1] = name;
		data2[data.length][2] = progressBar;
		data = data2;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public void removeRow(int row) {
		Object[][] data2 = new Object[data.length - 1][3];
		int loc = 0;
		for (int i = 0; i < data.length; i++) {
			if (i != row) {
				for (int j = 0; j < 3; j++) {
					data2[loc][j] = data[i][j];
				}
				loc++;
			}

		}
		data = data2;
	}

	public void removeRow(UUID code) {
		for (int i = 0; i < data.length; i++) {
			if (code.equals(data[i][0])) {
				removeRow(i);
			}
		}
	}

	public void removeOldRows(UUID[] codes) {
		for (int i = 0; i < codes.length; i++) {
			if (codes[i].equals(data[i][0])) {
				removeRow(i);
			}
		}
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}
class MediaItem
{
	FTPFile media;
	public MediaItem(FTPFile media)
	{
		this.media = media;
	}
	public String toString()
	{
		return media.getName();
	}
	public int getType()
	{
		return media.getType();
	}
	public String getName()
	{
		return media.getName();
	}
	public long getSize()
	{
		return media.getSize();
	}
}
class AudioTableModel extends AbstractTableModel {

	private String[] columnNames = { "Type", "Name", "Size"};
	private Object[][] data = new Object[0][0];

	public void update(Object[][] value) {
		data = value;
	}

	public void update(FTPFile[] value) {
		Object[][] newData = new Object[value.length][3];
		for (int i = 0; i < newData.length; i++) {
			newData[i][0] = "no image available";
			newData[i][1] = new MediaItem(value[i]);
			long size = value [i].getSize();
			size = size/100;
			newData[i][2] = size + "KB";
		}
		data = newData;
	}

	public void update(ArrayList<File> files) {
		System.out.println("updating");
		Object[][] newData = new Object[data.length][3];
		for (int i = 0; i < newData.length; i++) {
			newData[i][0] = data[i];
			for (int j = 0; j < files.size(); j++) {
				if (((String) data[i][1]).equals(((File) files.get(j))
						.getName())) {
					System.out.println("match");
					Image img = Toolkit.getDefaultToolkit().getImage(
							files.get(j).getAbsolutePath());
					ImageIcon icon = new ImageIcon(img);
					newData[i][0] = icon;
				}
			}
		}
		data = newData;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}