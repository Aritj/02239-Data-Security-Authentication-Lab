import java.rmi.RemoteException;
import java.util.Scanner;

import Server.Interface.IPrintServer;

public class UI {
	private Scanner scanner;

	public UI(Scanner scanner) {
		this.scanner = scanner;
	}

	public void startLoop(IPrintServer server) throws RemoteException {
		printOptions();

		while (! scanner.hasNext("exit")) {
			String input = scanner.nextLine();

            switch (input) {
                case "help":
					printOptions();
                    break;
                default:
                    break;
            }
		}
	}
	
	public void printOptions() {
	    System.out.println("+-----COMMAND --------------------DESCRIPTION-------------------+");
	    System.out.println("| print         │ prints file filename on the specified printer |");
	    System.out.println("| queue         │ lists the print queue for a given printer     |");
	    System.out.println("| topQueue      │ moves job to the top of the queue             |");
	    System.out.println("| start         │ starts the print server                       |");
	    System.out.println("| stop          │ stops the print se                            |");
	    System.out.println("| restart       │ stops, clears and restart server              |");
	    System.out.println("| status        │ prints status of printer                      |");
	    System.out.println("| help          │ prints the client options                     |");
	    System.out.println("| exit          │ exits application                             |");
	    System.out.println("+---------------------------------------------------------------+");
	}



	

}