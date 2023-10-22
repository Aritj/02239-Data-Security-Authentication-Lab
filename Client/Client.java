import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import Server.Interface.IPrintServer;

public class Client {
    private Scanner scanner = new Scanner(System.in);
    private UI ui = new UI(scanner);

    public void begin(int port) throws MalformedURLException, RemoteException, NotBoundException {
        IPrintServer printServer = (IPrintServer) Naming.lookup(String.format(
            "rmi://localhost:%d/printer",
            port
        ));

        printServer.start();
        ui.startLoop(printServer);
    }
}
