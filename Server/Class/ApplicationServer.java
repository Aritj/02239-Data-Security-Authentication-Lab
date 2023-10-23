package Server.Class;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Server.Interface.ILogger;

public class ApplicationServer {
	public final String serviceName;
	public final int port;
	public final ILogger logger;
	private Registry registry;

	public ApplicationServer(String serviceName, int port, ILogger logger) {
		this.serviceName = serviceName;
		this.port = port;
		this.logger = logger;
	}

	public void start() throws AccessException, RemoteException {
        registry = LocateRegistry.createRegistry(port);
		registry.rebind(serviceName, new PrintServer(logger));	
	}
	
	public void stop() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(registry, true);
	    System.exit(0);
	}
}