package Server.Interface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Server.Class.Session;

/**
 * Mock-up of a simple authenticated print server, such as a print server installed in a small company.
 */
public abstract interface IPrintServer extends Remote {
    /**
     * Prints file filename on the specified printer.
     * @param filename
     * @param printerName
     * @param username
     */
    abstract void print(String filename, String printerName, String username) throws RemoteException;
    
    /**
     * Lists the print queue for a given printer on the user's display in lines of the form "job number   file name".
     * @param printerName
     * @param username
     */
    abstract String queue(String printerName, String username) throws RemoteException;
    
    /**
     * Moves job to the top of the queue.
     * @param printerName
     * @param job
     * @param username
     */
    abstract void topQueue(String printerName, int job, String username) throws RemoteException;
    
    /**
     * Starts the print server.
     * @param username
     */
    abstract void start(String username) throws RemoteException;
    
    /**
     * Stops the print server.
     * @param username
     */
    abstract void stop(String username) throws RemoteException, NotBoundException;
    
    /**
     * Stops the print server, clears the print queue and starts the print server again.
     * @param username
     */
    abstract void restart(String username) throws RemoteException, NotBoundException;
    
    /**
     * Prints status of printer on the user's display.
     * @param printerName
     * @param username
     */
    abstract String status(String printerName, String username) throws RemoteException;
    
    /**
     * Prints the value of the parameter on the print server to the user's display.
     * @param parameter
     * @param username
     */
    abstract String readConfig(String parameter, String username) throws RemoteException;
    
    /**
     * Sets the parameter on the print server to value.
     * @param parameter
     * @param value
     * @param username
     */
    abstract void setConfig(String parameter, String value, String username) throws RemoteException;


    /**
     * Returns a list of the names of the printers.
     * @return
     */
    abstract List<String> getPrinterNames() throws RemoteException;

    /**
     * Authenticates a user based on username and password credentials.
     * @return Session is user is authenticated or null if credentials are invalid.
     */
    abstract Session authenticateUser(String username, String password) throws RemoteException, FileNotFoundException, IOException;
}
