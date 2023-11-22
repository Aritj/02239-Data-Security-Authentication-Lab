package Server.Class;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private HashMap<String, List<String>> rolePermissions = new HashMap<>();
    private HashMap<String, String> userRoles = new HashMap<>();

    protected PrintServer(ILogger logger) throws RemoteException, IOException {
        super();
        this.logger = logger;
        loadAccessPolicy(); // Load access control policy
        initialize();
    }

    private void loadAccessPolicy() throws IOException {
        boolean readingRoles = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("access_policy.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
    
                if (line.equals("[Roles]")) {
                    readingRoles = true;
                } else if (line.equals("[Users]")) {
                    readingRoles = false;
                } else if (line.contains("=")) {
                    String[] parts = line.split("=");
                    if (parts.length > 1) {
                        if (readingRoles) {
                            rolePermissions.put(parts[0].trim(), Arrays.asList(parts[1].trim().split(",\\s*")));
                        } else {
                            userRoles.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            }
        }
    }
    

    private boolean hasPermission(String username, String operation) {
        String role = userRoles.getOrDefault(username, "");
        List<String> permissions = rolePermissions.getOrDefault(role, Collections.emptyList());
        return permissions.contains(operation);
    }

    @Override
    public void print(String filename, String printerName, String username) throws RemoteException {

        if (!hasPermission(username, "print")) {
            throw new RemoteException("Access denied.");
        }


        IPrinter printer = printers.get(printerName);

        if (printer == null) {
            throw new RemoteException(NO_PRINTER_ERROR_MESSAGE);
        }

        printer.addToQueue(filename);
    }
 
    @Override
    public String queue(String printerName, String username) throws RemoteException {

        if (!hasPermission(username, "queue")) {
            throw new RemoteException("Access denied.");
        }


        IPrinter printer = printers.get(printerName);

        if (printer == null) {
            return NO_PRINTER_ERROR_MESSAGE;
        }

        if (printer.getQueue().isEmpty()) {
            return status(printerName, username);
        }
        
        return printer.getQueueAsString();
    }

    @Override
    public void topQueue(String printerName, int job, String username) throws RemoteException {

        if (!hasPermission(username, "topQueue")) {
            throw new RemoteException("Access denied.");
        }

        IPrinter printer = printers.get(printerName);

        if (printer == null) {
            return;
        }
            
        printer.topQueue(job);
    }

    @Override
    public void start(String username) throws RemoteException {

        if (!hasPermission(username, "start")) {
            throw new RemoteException("Access denied.");
        }

        if (isStarted) {
            return;
        }

        logger.log("PrintServer started.");
        printers.values().forEach(printer -> printer.start());
        isStarted = true;
        logger.log(username + " started PrintServer.");
    }

    @Override
    public void stop(String username) throws RemoteException, NotBoundException {

        if (!hasPermission(username, "stop")) {
            throw new RemoteException("Access denied.");
        }

        if (! isStarted) {
            return;
        }

        printers.values().forEach(printer -> printer.stop());
        isStarted = false;
        logger.log(username + "PrintServer stopped.");
    }

    @Override
    public void restart(String username) throws RemoteException, NotBoundException {

        if (!hasPermission(username, "restart")) {
            throw new RemoteException("Access denied.");
        }

        if (! isStarted) {
            return;
        }

        stop(username);
        printers.values().forEach(printer -> printer.clearQueue());
        start(username);
        logger.log(username + "PrintServer restarted.");
    }

    @Override
    public String status(String name, String username) throws RemoteException {

        if (!hasPermission(username, "status")) {
            throw new RemoteException("Access denied.");
        }

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
    public String readConfig(String parameter, String username) throws RemoteException {

        if (!hasPermission(username, "readConfig")) {
            throw new RemoteException("Access denied.");
        }

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
    public void setConfig(String parameter, String value, String username) throws RemoteException {

        if (!hasPermission(username, "setConfig")) {
            throw new RemoteException("Access denied.");
        }

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
