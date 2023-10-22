import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        IPrintServer printServer = (IPrintServer) Naming.lookup("rmi://localhost:5099/printer");
        String printer = "PrintServer"; // <-- part of assignment specification (don't know the use-case..)

        // Example use:
        System.out.println(printServer.status(printer));
        printServer.start();
        System.out.println(printServer.status(printer));
        printServer.topQueue(printer, 100);
        printServer.topQueue(printer, 200);
        printServer.topQueue(printer, 300);
        System.out.println(printServer.queue(printer));
        printServer.print("test.txt", printer);
        printServer.print("test2.txt", printer);
        
        // TODO: Create UI for Client?
    }
}
