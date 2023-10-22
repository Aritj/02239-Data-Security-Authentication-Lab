import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Mock-up of a simple authenticated print server, such as a print server installed in a small company.
 */
abstract interface IPrintServer extends Remote {
    /**
     * Prints file filename on the specified printer.
     * @param filename
     * @param printer
     */
    abstract void print(String filename, String printer) throws RemoteException;
    
    /**
     * Lists the print queue for a given printer on the user's display in lines of the form "job number   file name".
     * @param printer
     */
    abstract String queue(String printer) throws RemoteException;
    
    /**
     * Moves job to the top of the queue.
     * @param printer
     * @param job
     */
    abstract void topQueue(String printer, int job) throws RemoteException;
    
    /**
     * Starts the print server.
     */
    abstract void start() throws RemoteException;
    
    /**
     * Stops the print server.
     */
    abstract void stop() throws RemoteException, NotBoundException;
    
    /**
     * Stops the print server, clears the print queue and starts the print server again.
     */
    abstract void restart() throws RemoteException, NotBoundException;
    
    /**
     * Prints status of printer on the user's display.
     * @param printer
     */
    abstract String status(String printer) throws RemoteException;
    
    /**
     * Prints the value of the parameter on the print server to the user's display.
     * @param parameter
     */
    abstract String readConfig(String parameter) throws RemoteException;
    
    /**
     * Sets the parameter on the print server to value.
     * @param parameter
     * @param value
     */
    abstract void setConfig(String parameter, String value) throws RemoteException;
}
