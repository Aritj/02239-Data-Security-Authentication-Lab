import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.security.NoSuchAlgorithmException;

import Server.Class.ApplicationServer;
import Server.Class.Logger;
import Server.Interface.ILogger;

public class Main {
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, IOException {
		ILogger logger = new Logger();
		ApplicationServer server = new ApplicationServer("printer", 5099, logger);
		Client client = new Client(server);

		// CREDENTIALS FOR LOGIN (stored in 'credentials'):
		// admin / 123
		// test / login
		server.start();
		client.start();
	}
}
