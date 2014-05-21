package com.client;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import com.common.Contact;

/**
 *
 * @author Kev
 */
public class FtpServerModule
{

    private static FtpServerFactory serverFactory;
    private static FtpServer server;
    private Contact contact;
    
    
    public FtpServerModule(Contact contact) {
		super();
		this.contact = contact;
	}

	public void configure()
    {
        serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(21);

        // replace the default listener
        //serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File settings = new File(System.getenv("ULYSSES_HOME") + "\\myusers.properties");
        settings.delete();
        try {
			settings.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        userManagerFactory.setFile(new File(System.getenv("ULYSSES_HOME") + "\\myusers.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();
        try {
            serverFactory.setUserManager(um);
            BaseUser user = new BaseUser();
            user.setEnabled(true);
            user.setName(contact.getScreenName());
            System.out.println(contact.getFtpPassword());
            user.setPassword(contact.getFtpPassword());
            user.setHomeDirectory(System.getenv("ULYSSES_HOME") + "\\Library");
            um.save(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setUserManager(um);
    }

    public void startServer()
    {
        configure();

        server = serverFactory.createServer();
        try {
            // start the server
            server.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String [] args)
    {
        FtpServerModule serverModule = new FtpServerModule(new Contact());
        serverModule.configure();
        serverModule.startServer();
    }
}