import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Server.Class.ApplicationServer;

public class Main {
	static ApplicationServer server = new ApplicationServer("printer", 5099);
	static Client client = new Client(server);
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		server.start();
		client.begin();
	}
}
