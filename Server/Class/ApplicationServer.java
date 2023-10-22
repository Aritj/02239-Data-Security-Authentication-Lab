package Server.Class;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ApplicationServer {
	Registry registry;

	public void begin(int portnumber) throws AccessException, RemoteException {
		startServer(portnumber);	
	}

	public void startServer(int port) throws AccessException, RemoteException {
        registry = LocateRegistry.createRegistry(port);
		registry.rebind("printer", new PrintServer());	
	}
	
	public void stopServer() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(registry, true);
	    System.exit(0);
	}
}