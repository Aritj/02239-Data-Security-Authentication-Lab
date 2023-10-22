import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        IPrintServer printServer = (IPrintServer) Naming.lookup("rmi://localhost:5099/printer");
        String printer = "PrintServer";

        printServer.start();
        printServer.print("test2.txt", printer);
        printServer.stop();
    }
}
