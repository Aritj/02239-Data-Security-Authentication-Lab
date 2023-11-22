import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import Server.Class.ApplicationServer;
import Server.Interface.ILogger;
import Server.Interface.IPrintServer;

public class Client {
    private ApplicationServer server;
    private ILogger logger;

    public Client(ApplicationServer server, ILogger logger) {
        this.server = server;
        this.logger = logger;
    }

    public void start() throws MalformedURLException, RemoteException, NotBoundException {
        IPrintServer printServer = (IPrintServer) Naming.lookup(String.format(
                "rmi://localhost:%d/%s",
                server.port,
                server.serviceName));
        Scanner scanner = new Scanner(System.in);

        UI ui = new UI(scanner, logger);
        ui.activate(printServer); // <-- loop
        server.stop();
    }
}
