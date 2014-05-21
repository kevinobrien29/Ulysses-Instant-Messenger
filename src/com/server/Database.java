package com.server;

/**
 * This class is used to access the database. It is irrelevant for
 * this assignment.
 * @author Kev
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import com.common.Account;
import com.common.Contact;
import com.common.ContactsList;

public class Database implements Cloneable, Serializable {

	private static final long serialVersionUID = -4115724068620943128L;
	private Connection database;

	public Database() {
		try {
			// Step 1: Load the JDBC driver.
			Class.forName("com.mysql.jdbc.Driver");
			// Step 2: Establish the connection to the database.
			String url = "jdbc:mysql://localhost:3306/myDatabase";
			database = DriverManager.getConnection(url, "root", "case");
			try {
				Statement st = database.createStatement();
				String table = "CREATE TABLE users(userID VARCHAR(50) PRIMARY KEY, Name varchar(20))";
				st.executeUpdate(table);
			} catch (SQLException s) {
			}
			try {
				Statement st = database.createStatement();
				String table = "CREATE TABLE Authentication(userID VARCHAR(50) PRIMARY KEY, Password varchar(20))";
				st.executeUpdate(table);
			} catch (SQLException s) {
			}
			try {
				Statement st = database.createStatement();
				String table = "CREATE TABLE UserStatus(userID VARCHAR(50) PRIMARY KEY, Status varchar(10), IP varchar(20), FTPPASSWORD varchar(20))";
				st.executeUpdate(table);
			} catch (SQLException s) {
			}
			try {
				Statement st = database.createStatement();
				String table = "CREATE TABLE friends(userID VARCHAR(50), friendsID VARCHAR(50))";
				st.executeUpdate(table);
			} catch (SQLException s) {
			}
			try {
				Statement st = database.createStatement();
				String table = "UPDATE userstatus SET status = 'offline', ip = ''";
				int rows = st.executeUpdate(table);
			} catch (SQLException s) {
				s.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addUser(String name, String password, String ftpPassword) {
		if (!userExists(name)) {
			try {
				Statement insert = database.createStatement();
				insert.executeUpdate("INSERT INTO users VALUES (UUID()," + "\""
						+ name + "\"" + ")");
				insert
						.executeUpdate("INSERT INTO Authentication VALUES ((SELECT userID from users WHERE name=\""
								+ name + "\")," + "\"" + password + "\")");
				insert
						.executeUpdate("INSERT INTO UserStatus VALUES ((SELECT userID from users WHERE name=\""
								+ name
								+ "\"),"
								+ "\"offline\""
								+ ",\"0.0.0.0\"" + ",\"" + ftpPassword + "\")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void addFriend(String name, String userName, String password) {
		if (isPassword(name, password) && userExists(name)) {
			try {
				Statement insert = database.createStatement();
				insert
						.executeUpdate("INSERT INTO friends VALUES ((SELECT userID from users WHERE name=\""
								+ name
								+ "\"),(SELECT userID from users WHERE name=\""
								+ userName + "\"))");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteFriend(String name, String userName, String password) {
		if (isPassword(name, password) && userExists(name)) {
			try {
				Statement insert = database.createStatement();
				insert
						.executeUpdate("DELETE FROM friends WHERE friendsID=((SELECT userID from users WHERE name=\""
								+ name
								+ "\") and userID=(SELECT userID from users WHERE name=\""
								+ userName + "\"))");
				insert
						.executeUpdate("DELETE FROM friends WHERE userID=((SELECT userID from users WHERE name=\""
								+ name
								+ "\") and friendsID=(SELECT userID from users WHERE name=\""
								+ userName + "\"))");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Account getAccount(String name, String password) {
		ArrayList<Contact> arrayList = null;
		Account account = null;
		if (isPassword(name, password)) {
			arrayList = new ArrayList<Contact>();
			try {
				Statement select = database.createStatement();
				ResultSet result = select
						.executeQuery("SELECT"
								+ " users.Name, friends.friendsID, UserStatus.IP, UserStatus.FTPPASSWORD, UserStatus.Status FROM friends, users, UserStatus WHERE UserStatus.userID=friends.friendsID and users.userID=friends.friendsID and friends.userID=(SELECT userID from users WHERE name=\""
								+ name + "\")");
				while (result.next()) {
					Boolean online = false;
					if (result.getString("Status").equals("online")) {
						online = true;
					}
					Contact contact = new Contact(result.getString("Name"),
							result.getString("friendsID"), result
									.getString("FTPPASSWORD"), result
									.getString("IP"), online);
					arrayList.add(contact);
				}
				Statement info = database.createStatement();
				ResultSet infoResult = info
						.executeQuery("SELECT"
								+ " users.userID, users.Name, UserStatus.IP, UserStatus.FTPPASSWORD FROM users, UserStatus WHERE users.userID=UserStatus.userID and UserStatus.userID=(SELECT userID from users WHERE name=\""
								+ name + "\")");
				infoResult.next();
				account = new Account(infoResult.getString("userID"),
						infoResult.getString("Name"), new ContactsList(
								arrayList), infoResult.getString("IP"),
						infoResult.getString("FTPPASSWORD"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return account;
	}

	public Account getAccount(String name) {
		ArrayList<Contact> arrayList = null;
		Account account = null;
		arrayList = new ArrayList<Contact>();
		try {
			Statement select = database.createStatement();
			ResultSet result = select
					.executeQuery("SELECT"
							+ " users.Name, friends.friendsID, UserStatus.IP, UserStatus.FTPPASSWORD, UserStatus.Status FROM friends, users, UserStatus WHERE UserStatus.userID=friends.friendsID and users.userID=friends.friendsID and friends.userID=(SELECT userID from users WHERE name=\""
							+ name + "\")");
			while (result.next()) {
				Boolean online = false;
				if (result.getString("Status").equals("online")) {
					online = true;
				}
				Contact contact = new Contact(result.getString("Name"), result
						.getString("friendsID"), result
						.getString("FTPPASSWORD"), result.getString("IP"),
						online);
				arrayList.add(contact);
			}
			Statement info = database.createStatement();
			ResultSet infoResult = info
					.executeQuery("SELECT"
							+ " users.userID, users.Name, UserStatus.IP, UserStatus.FTPPASSWORD FROM users, UserStatus WHERE users.userID=UserStatus.userID and UserStatus.userID=(SELECT userID from users WHERE name=\""
							+ name + "\")");
			infoResult.next();
			account = new Account(infoResult.getString("userID"), infoResult
					.getString("Name"), new ContactsList(arrayList), infoResult
					.getString("IP"), infoResult.getString("FTPPASSWORD"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return account;
	}

	public String getIP(String name) {
		ArrayList<Contact> arrayList = null;
		Account account = null;
		arrayList = new ArrayList<Contact>();
		try {
			Statement select = database.createStatement();
			ResultSet result = select
					.executeQuery("SELECT"
							+ " users.Name, friends.friendsID, UserStatus.IP, UserStatus.FTPPASSWORD, UserStatus.Status FROM friends, users, UserStatus WHERE UserStatus.userID=friends.friendsID and users.userID=friends.friendsID and friends.userID=(SELECT userID from users WHERE name=\""
							+ name + "\")");
			while (result.next()) {
				Boolean online = false;
				if (result.getString("Status").equals("online")) {
					online = true;
				}
				Contact contact = new Contact(result.getString("Name"), result
						.getString("friendsID"), result
						.getString("FTPPASSWORD"), result.getString("IP"),
						online);
				arrayList.add(contact);
			}
			Statement info = database.createStatement();
			ResultSet infoResult = info
					.executeQuery("SELECT"
							+ " users.userID, users.Name, UserStatus.IP, UserStatus.FTPPASSWORD FROM users, UserStatus WHERE users.userID=UserStatus.userID and UserStatus.userID=(SELECT userID from users WHERE name=\""
							+ name + "\")");
			infoResult.next();
			return infoResult.getString("IP");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public UUID getID(String name, String password) {
		UUID code = null;
		if (isPassword(name, password)) {
			try {
				Statement info = database.createStatement();
				ResultSet infoResult = info.executeQuery("SELECT"
						+ " users.userID FROM users WHERE users.name =\""
						+ name + "\"");
				infoResult.next();
				code = code.fromString(infoResult.getString("userID"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return code;
	}

	public void addFriendsList(String name, String password,
			ContactsList contact) {
		if (!userExists(name)) {
			try {
				Statement insert = database.createStatement();
				insert.executeUpdate("INSERT INTO users VALUES (UUID()," + "\""
						+ name + "\"" + ")");
				insert
						.executeUpdate("INSERT INTO Authentication VALUES ((SELECT userID from users WHERE name=\""
								+ name + "\")," + "\"" + password + "\"" + ")");
				insert
						.executeUpdate("INSERT INTO UserStatus VALUES ((SELECT userID from users WHERE name=\""
								+ name
								+ "\"),"
								+ "\"offline\""
								+ ",\"0.0.0.0\")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setOnline(String name, String password, String IP) {
		if (isPassword(name, password)) {
			try {
				Statement update = database.createStatement();
				update
						.executeUpdate("UPDATE userStatus SET Status=\"online\", IP=\""
								+ IP
								+ "\" WHERE userID IN(SELECT userID from users WHERE name=\""
								+ name + "\")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setOffline(String name, String password) {
		if (isPassword(name, password)) {
			try {
				Statement update = database.createStatement();
				update
						.executeUpdate("UPDATE userStatus SET Status=\"offline\", IP=\"0.0.0.0\" WHERE userID IN(SELECT userID from users WHERE name=\""
								+ name + "\")");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isPassword(String name, String password) {
		try {
			Statement select = database.createStatement();
			ResultSet result = select
					.executeQuery("SELECT Password FROM Authentication WHERE userID IN (SELECT userID from users WHERE name=\""
							+ name + "\")");
			if (result.next()) {
				if (result.getString("Password").equals(password)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean userExists(String name) {
		try {
			Statement select = database.createStatement();
			ResultSet result = select
					.executeQuery("SELECT Name FROM users WHERE Name =\""
							+ name + "\"");
			if (result.next()) {
				if (result.getString("Name").equals(name)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void deleteDatabase() {
		try {
			Statement select = database.createStatement();
			String sql = "DROP DATABASE myDatabase";
			select.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createDatabase() {
		try {
			Statement select = database.createStatement();
			String sql = "CREATE DATABASE myDatabase";
			select.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Database database = new Database();
		database.deleteDatabase();
		database.createDatabase();
		database = new Database();
		database.addUser("Username", "Password", "password");
		database.addUser("Susan", "Password", "password");
		database.addUser("Ron", "Password", "password");
		database.addUser("Barrie", "Password", "password");
		database.addUser("David", "Password", "password");
		database.addUser("Fitzy", "Password", "password");
		database.addUser("Matt", "Password", "password");
		database.addUser("Anna", "Password", "password");
		database.addUser("Ais", "Password", "password");
		database.addUser("Armours", "Password", "password");
		database.addFriend("Username", "Susan", "Password");
		database.addFriend("Susan", "Username", "Password");
		Account account = database.getAccount("Username", "Password");
	}
}