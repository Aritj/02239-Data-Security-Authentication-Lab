import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Scanner;

import Server.Interface.IPrintServer;

public class UI {
	private Scanner scanner;
	private HashMap<String, Runnable> logicHashMap = new HashMap<>();

	public UI(Scanner scanner) {
		this.scanner = scanner;
	}

	public void activate(IPrintServer server) throws RemoteException, NotBoundException {
		initializeLogicHashMap(server);
		help();
		
		while (! scanner.hasNext("exit")) {
			String input = scanner.nextLine().trim().toLowerCase();
			Runnable method = logicHashMap.get(input);

			if (method == null) {
				help();
				continue;
			}

			method.run();
		}
	}

	private void initializeLogicHashMap(IPrintServer server) {
		logicHashMap.put("print", () -> print(server));
		logicHashMap.put("queue", () -> queue(server));
		logicHashMap.put("topqueue", () -> topQueue(server));
		logicHashMap.put("start", () -> start(server));
		logicHashMap.put("stop", () -> stop(server));
		logicHashMap.put("restart", () -> restart(server));
		logicHashMap.put("status", () -> status(server));
	}
	
	private void print(IPrintServer server) {
		String filename = printMessageGetStringInput("Enter filename: ");
		String printerName = printMessageGetStringInput("Enter printer name: ");
		
		try {
			server.print(filename, printerName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void queue(IPrintServer server) {
		String printerName = printMessageGetStringInput("Enter printer name: ");

		try {
			System.out.println(server.queue(printerName));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void topQueue(IPrintServer server) {
		String printerName = printMessageGetStringInput("Enter printer name: ");
		int jobNumber = printMessageGetIntInput("Write job number: ");

		System.out.println(String.format("Job number %d sent to top of queue on %s printer.",
			jobNumber,
			printerName
		));

		try {
			server.topQueue(printerName, jobNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void start(IPrintServer server) {
		System.out.println("Starting server.");

		try {
			server.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void stop(IPrintServer server) {
		System.out.println("Stopping server.");

		try {
			server.stop();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void restart(IPrintServer server) {
		System.out.println("Restarting server.");

		try {
			server.restart();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void status(IPrintServer server) {
		String printerName = printMessageGetStringInput("Write name of printer: ");

		try {
			System.out.println(server.status(printerName));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void help() {
		System.out.println("+-----COMMAND --------------------DESCRIPTION-------------------+");
		System.out.println("| Print         │ Prints file filename on the specified printer |");
		System.out.println("| Queue         │ Lists the print queue for a given printer     |");
		System.out.println("| TopQueue      │ Moves job to the top of the queue             |");
		System.out.println("| Start         │ Starts the print server                       |");
		System.out.println("| Stop          │ Stops the print se                            |");
		System.out.println("| Restart       │ Stops, clears and restart server              |");
		System.out.println("| Status        │ Prints status of printer                      |");
		System.out.println("| Help          │ Prints the client options                     |");
		System.out.println("| Exit          │ Exits the application                         |");
		System.out.println("+---------------------------------------------------------------+");
	}

	private String printMessageGetStringInput(String message) {
		System.out.print(message);

		return scanner.nextLine();
	}

	private int printMessageGetIntInput(String message) {
		System.out.print(message);

		return Integer.parseInt(scanner.nextLine());
	}
}
