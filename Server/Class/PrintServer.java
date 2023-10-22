package Server.Class;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import Server.Interface.ILogger;
import Server.Interface.IPrintServer;
import Server.Interface.IPrinter;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {
    public final static String ERROR_MESSAGE = "N/A";

    private ILogger logger = new Logger();
    private HashMap<String, IPrinter> printers = new HashMap<>();
    private HashMap<String, String> configHashMap = new HashMap<>();

    protected PrintServer() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printerName) throws RemoteException {
        IPrinter printer = printers.get(printerName);

        if (printer == null) {
            return;
        }

        printer.addToQueue(filename);
    }
 
    @Override
    public String queue(String printerName) throws RemoteException {
        IPrinter printer = printers.get(printerName);
        
        return printer == null
            ? ERROR_MESSAGE
            : printer.getQueueAsString();
    }

    @Override
    public void topQueue(String printerName, int job) throws RemoteException {
        IPrinter printer = printers.get(printerName);

        if (printer == null) {
            return;
        }
            
        printer.topQueue(job);
    }

    @Override
    public void start() throws RemoteException {
        logger.log("PrintServer starting.");
        initialize();
    }

    @Override
    public void stop() throws RemoteException, NotBoundException {
        logger.log("PrintServer stopping.");
    }

    @Override
    public void restart() throws RemoteException, NotBoundException {
        logger.log("PrintServer restarting.");
        stop();
        printers.clear();
        start();
    }

    @Override
    public String status(String name) throws RemoteException {
        IPrinter printer = printers.get(name);

        return printer == null
            ? ERROR_MESSAGE
            : String.format(
                "%s status: %s.", 
                printer.getPrinterName(), 
                printer.status() 
                    ? "active" 
                    : "inactive"
            );
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        return String.format(
            "%s: %s", 
            parameter,
            configHashMap.get(parameter)
        );
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        configHashMap.put(parameter, value);

        logger.log(String.format(
            "Added {%s: %s} mapping.",
            parameter,
            value
        ));
    }

    private void initialize() {
        for (String name : new String[]{"Home", "Office", "School"}) {
            printers.put(name, new Printer(name, logger));
        }
    }
}
