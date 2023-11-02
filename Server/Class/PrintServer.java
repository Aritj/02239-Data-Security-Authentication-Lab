package Server.Class;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Cryptography.Cryptography;
import Server.Interface.ILogger;
import Server.Interface.IPrintServer;
import Server.Interface.IPrinter;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {
    private String NO_PRINTER_ERROR_MESSAGE = "PrintServer couldn't find that printer.";
    private String MISSING_KEY_ERROR_MESSAGE = "PrintServer couldn't find that key.";
    private Boolean isStarted = false;
    private ILogger logger;
    private HashMap<String, IPrinter> printers = new HashMap<>();
    private HashMap<String, String> configHashMap = new HashMap<>();

    protected PrintServer(ILogger logger) throws RemoteException {
        super();
        this.logger = logger;
        initialize();
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

        if (printer == null) {
            return NO_PRINTER_ERROR_MESSAGE;
        }

        if (printer.getQueue().isEmpty()) {
            return status(printerName);
        }
        
        return printer.getQueueAsString();
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
        if (isStarted) {
            return;
        }

        logger.log("PrintServer started.");
        printers.values().forEach(printer -> printer.start());
        isStarted = true;
    }

    @Override
    public void stop() throws RemoteException, NotBoundException {
        if (! isStarted) {
            return;
        }

        printers.values().forEach(printer -> printer.stop());
        isStarted = false;
        logger.log("PrintServer stopped.");
    }

    @Override
    public void restart() throws RemoteException, NotBoundException {
        if (! isStarted) {
            return;
        }

        stop();
        printers.values().forEach(printer -> printer.clearQueue());
        start();
        logger.log("PrintServer restarted.");
    }

    @Override
    public String status(String name) throws RemoteException {
        IPrinter printer = printers.get(name);

        return printer == null
            ? NO_PRINTER_ERROR_MESSAGE
            : String.format(
                "%s printer has %d jobs in queue.", 
                printer.getPrinterName(), 
                printer.getQueue().size()
            );
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        if (! configHashMap.containsKey(parameter)) {
            return MISSING_KEY_ERROR_MESSAGE;
        }

        return String.format(
            "{%s: %s}", 
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

    @Override
    public List<String> getPrinterNames() throws RemoteException {
        return new ArrayList<String>(printers.keySet());
    }

    @Override
    public Session authenticateUser(String username, String password) throws FileNotFoundException, IOException {
        if (! Cryptography.authenticateUser(username, password)) {
            return null;
        }
        
        logger.log(String.format(
            "%s has logged in.",
            username
        ));

        return new Session(username);
    }
}
