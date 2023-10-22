import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
    private boolean isActive = false;
    private Stack<Integer> printQueue = new Stack<>();
    private HashMap<String, String> configHashMap = new HashMap<>();
    private static Registry registry;

    public static void main(String[] args) throws RemoteException {
        registry = LocateRegistry.createRegistry(5099);
        registry.rebind("printer", new PrintServer());
    }
    
    protected PrintServer() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (! isActive) {
            return;
        }
        
        try (Scanner scanner = new Scanner(new File(filename))) {
            StringBuilder stringBuilder = new StringBuilder(String.format(
                "Printing '%s'",
                filename 
            ));

            while (scanner.hasNextLine()) {
                stringBuilder.append("\n" + scanner.nextLine());
            }

            log(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            log(e.toString());
        }
    }
 
    @Override
    public String queue(String printer) throws RemoteException {
        if (! isActive) {
            return status(printer);
        }

        StringBuilder stringBuilder = new StringBuilder("Print queue:");

        for (int i = 0; i < printQueue.size(); i++) {
            stringBuilder.append(String.format(
                "\n\t<%d>   <%s>.",
                i,
                printQueue.get(i)
            ));
        }

        return stringBuilder.toString();
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        if (! isActive) {
            return;
        }

        printQueue.push(job);
    }

    @Override
    public void start() throws RemoteException {
        log("PrintServer starting.");
        isActive = true;
    }

    @Override
    public void stop() throws RemoteException, NotBoundException {
        log("PrintServer stopping.");
        isActive = false;
    }

    @Override
    public void restart() throws RemoteException, NotBoundException {
        log("PrintServer restarting.");
        stop();
        printQueue.clear();
        start();
    }

    @Override
    public String status(String printer) throws RemoteException {
        return String.format(
            "%s status: %s.", 
            printer,
            isActive ? "active" : "inactive"
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

        log(String.format(
            "Added {%s: %s} mapping.",
            parameter,
            value
        ));
    }

    /**
     * It is sufficient that the print server records the invocation of a particular operation in a logfile or prints it on the console.
     * @param message
     */
    private void log(String message) {
        String formattedLogMessage = String.format(
            "|%s| %s",
            dtf.format(LocalDateTime.now()),
            message
        );

        System.out.println(formattedLogMessage);
    }
}
