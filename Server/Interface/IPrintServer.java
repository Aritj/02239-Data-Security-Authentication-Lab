package Server.Interface;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Mock-up of a simple authenticated print server, such as a print server installed in a small company.
 */
public abstract interface IPrintServer extends Remote {
    /**
     * Prints file filename on the specified printer.
     * @param filename
     * @param printerName
     */
    abstract void print(String filename, String printerName) throws RemoteException;
    
    /**
     * Lists the print queue for a given printer on the user's display in lines of the form "job number   file name".
     * @param printerName
     */
    abstract String queue(String printerName) throws RemoteException;
    
    /**
     * Moves job to the top of the queue.
     * @param printerName
     * @param job
     */
    abstract void topQueue(String printerName, int job) throws RemoteException;
    
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
     * @param printerName
     */
    abstract String status(String printerName) throws RemoteException;
    
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


    /**
     * Returns a list of the names of the printers.
     * @return
     */
    abstract List<String> getPrinterNames() throws RemoteException;
}
