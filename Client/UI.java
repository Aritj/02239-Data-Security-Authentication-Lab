import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Server.Interface.IPrintServer;

public class UI {
	int commandIndex = 0;
	String terminalString = "Client";
	private Scanner scanner;
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
		put("show", "Shows a list of the available printers");
		put("print", "Prints file filename on the specified printer");
		put("queue", "Lists the print queue for a given printer");
		put("topqueue", "Moves job to the top of the queue");
		put("start", "Starts the print server");
		put("stop", "Stops the print server");
		put("restart", "Stops, clears and restart server");
		put("status", "Prints status of printer");
		put("help", "Prints the client options");
		put("exit", "Exits the application");
	}};

	public UI(Scanner scanner) {
		this.scanner = scanner;
	}

	public void activate(IPrintServer server) throws RemoteException, NotBoundException {
		initializeLogicHashMap(server);
		initializeColorHashMap();
		help();
		printTerminal();

		String input;
		while (! (input = scanner.nextLine().trim()).equalsIgnoreCase("exit")) {
			if (input.isEmpty()) {
				printTerminal();
				continue;
			}

			Runnable method = logicHashMap.getOrDefault(input, () -> help());

			method.run();
			commandIndex++;
			printTerminal();
		}
	}

	private void initializeColorHashMap() {
		colorHashMap.put("reset", "\u001B[0m");
		colorHashMap.put("black", "\u001B[30m");
		colorHashMap.put("red", "\u001B[31m");
		colorHashMap.put("green", "\u001B[32m");
		colorHashMap.put("yellow", "\u001B[33m");
		colorHashMap.put("blue", "\u001B[34m");
		colorHashMap.put("purple", "\u001B[35m");
		colorHashMap.put("cyan", "\u001B[36m");
		colorHashMap.put("white", "\u001B[37m");
	}

	private void initializeLogicHashMap(IPrintServer server) {
		logicHashMap.put("help", () -> help());
		logicHashMap.put("show", () -> show(server));
		logicHashMap.put("print", () -> print(server));
		logicHashMap.put("queue", () -> queue(server));
		logicHashMap.put("topqueue", () -> topQueue(server));
		logicHashMap.put("start", () -> start(server));
		logicHashMap.put("stop", () -> stop(server));
		logicHashMap.put("restart", () -> restart(server));
		logicHashMap.put("status", () -> status(server));
	}
	
	private void print(IPrintServer server) {
		String printerName = getPrinterName(server);
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
		int jobNumber = printMessageGetIntInput(colorText("green","Write job number> "));


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

	private String getPrinterName(IPrintServer server) {
		try {
			List<String> printerNames = server.getPrinterNames();
			show(server);
			printTerminal("Printer");
			String input = scanner.nextLine().trim();
			
			return printerNames.contains(input)
				? input
				: printerNames.get(tryParseInteger(input));
		} catch (Exception e) {
			return null;
		}
	}

	private void show(IPrintServer server) {
		try {
			StringBuilder stringBuilder = new StringBuilder("Available printers:");

			for (int i = 0; i < server.getPrinterNames().size(); i++) {
				stringBuilder.append(String.format(
					"\n\t%d: %s",
					i,
					server.getPrinterNames().get(i)
				));
			}

			System.out.println(colorText("cyan", stringBuilder.toString()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private Integer tryParseInteger(String intAsString) {
		try {
			return Integer.parseInt(intAsString);
		} catch (Exception e) {
			return null;
		}
	}

	private void printTerminal(String terminalString) {
		String tempTerminalString = this.terminalString;
		this.terminalString = terminalString;
		printTerminal();
		this.terminalString = tempTerminalString;
	}

	private void printTerminal() {
		String terminalFormat = "User@%1$-7s(%2$d) ~ ";
		String coloredTerminal = colorText("green", String.format(
			terminalFormat,
			terminalString,
			commandIndex
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
