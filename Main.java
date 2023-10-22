import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Server.Class.ApplicationServer;


public class Main {
    static int port = 5099;
	
	static ApplicationServer server = new ApplicationServer();
	static Client client = new Client();
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		server.begin(port);
		client.begin(port);
		server.stopServer();
	}
}
