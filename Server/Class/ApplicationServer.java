package Server.Class;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ApplicationServer {
	public final String serviceName;
	public final int port;
	private Registry registry;

	public ApplicationServer(String serviceName, int port) {
		this.serviceName = serviceName;
		this.port = port;
	}

	public void start() throws AccessException, RemoteException {
        registry = LocateRegistry.createRegistry(port);
		registry.rebind(serviceName, new PrintServer());	
	}
	
	public void stop() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(registry, true);
	    System.exit(0);
	}
}