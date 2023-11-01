import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Server.Class.Session;
import Server.Interface.IPrintServer;

public class UI {
	private List<String> printerNames;
	private Scanner scanner;
	private int commandIndex = 0;
	private Session session = null;
	private String terminalName = "PrintServer";
	private HashMap<String, Runnable> logicHashMap = new HashMap<>();
	private HashMap<String, String> colorHashMap = new HashMap<>() {{
		put("reset", "\u001B[0m");
		put("black", "\u001B[30m");
		put("red", "\u001B[31m");
		put("green", "\u001B[32m");
		put("yellow", "\u001B[33m");
		put("blue", "\u001B[34m");
		put("purple", "\u001B[35m");
		put("cyan", "\u001B[36m");
		put("white", "\u001B[37m");
	}};
	private HashMap<String, String> helpHashMap = new HashMap<>() {{
		put("print", "Prints file filename on the specified printer");
		put("queue", "Lists the print queue for a given printer");
		put("topqueue", "Moves job to the top of the queue");
		put("start", "Starts the print server");
		put("stop", "Stops the print server");
		put("restart", "Stops, clears and restart server");
		put("status", "Prints status of printer");
		put("readconfig", "Print a parameter value");
		put("setconfig", "Set a parameter value");
		put("help", "Prints the client options");
		put("clear", "Clears the user interface.");
		put("show", "Shows a list of the available printers");
		put("exit", "Exits the application");
	}};

	public UI(Scanner scanner) {
		this.scanner = scanner;
	}

	public void activate(IPrintServer server) throws RemoteException, NotBoundException {
		initialize(server);

		String input;
		while (! (input = scanner.nextLine().trim()).equalsIgnoreCase("exit")) {
			login(server);

			if (input.isEmpty()) {
				printTerminal();
				continue;
			}

			Runnable method = logicHashMap.get(input);

			if (method == null) {
				unknown(input);
				printTerminal();
				continue;
			}

			method.run();
			commandIndex++;
			printTerminal();
		}
	}

	private void initialize(IPrintServer server) {
		try {
			this.printerNames = server.getPrinterNames();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		initializeLogicHashMap(server);
		login(server);
		help();
		printTerminal();
	}

	private void login(IPrintServer server) {
		while (session == null || ! session.getSessionState()) {
			System.out.print(colorText("yellow", "Enter username: "));
			String username = scanner.nextLine();
			System.out.print(colorText("yellow", "Enter password: "));
			String password = scanner.nextLine();
			
			try {
				this.session = server.authenticateUser(username, password);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (session == null) {
				System.out.println(colorText("red", "Invalid credentials!"));
			}
		}
	}

	private void unknown(String command) {
		System.out.println(colorText("red", String.format(
			"'%s' is an unknown command!",
			command
		)));
	}

	private void initializeLogicHashMap(IPrintServer server) {
		logicHashMap.put("print", () -> print(server));
		logicHashMap.put("queue", () -> queue(server));
		logicHashMap.put("topqueue", () -> topQueue(server));
		logicHashMap.put("start", () -> start(server));
		logicHashMap.put("stop", () -> stop(server));
		logicHashMap.put("restart", () -> restart(server));
		logicHashMap.put("status", () -> status(server));
		logicHashMap.put("readconfig", () -> readConfig(server));
		logicHashMap.put("setconfig", () -> setConfig(server));
		logicHashMap.put("clear", () -> clear());
		logicHashMap.put("help", () -> help());
		logicHashMap.put("show", () -> show(server));
	}

	private void setConfig(IPrintServer server) {
		String parameter = getParameterName(server);

		System.out.print(colorText("green", String.format(
		"%s value> ",
			parameter
		)));

		String value = scanner.nextLine().trim();

		try {
			server.setConfig(parameter, value);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void readConfig(IPrintServer server) {
		String parameter = getParameterName(server);
		
		try {
			System.out.println(colorText("cyan", server.readConfig(parameter)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void clear() {
		System.out.print("\033[H\033[2J");
	}
	
	private void print(IPrintServer server) {
		String printerName = getPrinterName(server);

		if (! printerNames.contains(printerName)) {
			return;
		}

		String filename = printMessageGetStringInput(colorText("green", "Enter filename> "));
		
		try {
			server.print(filename, printerName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void queue(IPrintServer server) {
		String printerName = getPrinterName(server);

		try {
			System.out.println(colorText("cyan", server.queue(printerName)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void topQueue(IPrintServer server) {
		String printerName = getPrinterName(server);
		int jobNumber = printMessageGetIntInput(colorText("green","Enter job number> "));

		try {
			server.topQueue(printerName, jobNumber);
			System.out.println(colorText("cyan", String.format("Job number %d sent to top of queue on %s printer.",
				jobNumber,
				printerName
			)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void start(IPrintServer server) {
		try {
			server.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void stop(IPrintServer server) {
		try {
			server.stop();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void restart(IPrintServer server) {
		System.out.println(colorText("cyan", "Restarting server."));

		try {
			server.restart();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void status(IPrintServer server) {
		String printerName = getPrinterName(server);

		try {
			System.out.println(colorText("cyan", server.status(printerName)));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void help() {
		String header = "+-----COMMAND ---------------------DESCRIPTION--------------------+";
		String format = "| %1$-15s | %2$-45s |\n";
		String footer = "+-----------------------------------------------------------------+";

		StringBuilder stringBuilder = new StringBuilder(header + "\n");
		helpHashMap.forEach((key, value) -> {
			stringBuilder.append(String.format(
				format,
				key,
				value
			));
		});
		stringBuilder.append(footer);

		System.out.println(colorText("cyan", stringBuilder.toString()));
	}

	private String printMessageGetStringInput(String message) {
		System.out.print(message);

		return scanner.nextLine();
	}

	private int printMessageGetIntInput(String message) {
		System.out.print(message);

		return Integer.parseInt(scanner.nextLine());
	}


	private String getParameterName(IPrintServer server) {
		System.out.print(colorText("green", "Parameter name> "));
		return scanner.nextLine().trim();
	}

	private String getPrinterName(IPrintServer server) {
		show(server);
		System.out.println(colorText("cyan", "Choose a printer."));
		printTerminal();

		String input = scanner.nextLine().trim();

		if (input == null) {
			return null;
		}

		if (printerNames.contains(input)) {
			return null;
		}

		int intInput = tryParseInteger(input);

		return intInput < 0 || printerNames.size() <= intInput
			? null
			: printerNames.get(intInput);
	}

	private void show(IPrintServer server) {
		StringBuilder stringBuilder = new StringBuilder("Available printers:");

		for (int i = 0; i < printerNames.size(); i++) {
			stringBuilder.append(String.format(
				"\n\t%d: %s",
				i,
				printerNames.get(i)
			));
		}

		System.out.println(colorText("cyan", stringBuilder.toString()));
	}

	private Integer tryParseInteger(String intAsString) {
		try {
			return Integer.parseInt(intAsString);
		} catch (Exception e) {
			return -1;
		}
	}

	private void printTerminal() {
		String terminalFormat = "(%1$02d) %2$s@%3$-7s ~ ";
		String coloredTerminal = colorText("green", String.format(
			terminalFormat,
			commandIndex,
			session.getUid(),
			terminalName
		));

		System.out.print(coloredTerminal);
	}

	private String colorText(String color, String message) {
		return String.format(
			"%s%s%s",
			colorHashMap.get(color),
			message,
			colorHashMap.get("reset")
		);
	}
}
