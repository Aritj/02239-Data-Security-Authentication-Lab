package Server.Class;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Server.Interface.ILogger;
import Server.Interface.IPrinter;

class Printer implements IPrinter {
    private ILogger logger;
    private ArrayList<String> queue = new ArrayList<String>();
	private String name;

    public Printer(String name, ILogger logger) {
		this.name = name;
        this.logger = logger;
	}

    @Override
    public String getPrinterName() {
        return name;
    }

    @Override
    public void setPrinterName(String name) {
        this.name = name;
    }

    @Override
    public ArrayList<String> getQueue() {
        return queue;
    }

    @Override
    public String getQueueAsString() {
        StringBuilder stringBuilder = new StringBuilder("Print queue:");

        for (int i = 0; i < queue.size(); i++) {
            stringBuilder.append(String.format(
                "\n\t<%d>   <%s>.",
                i,
                queue.get(i)
            ));
        }
        
        return stringBuilder.toString();
    }

    @Override
    public void addToQueue(String job) {
        queue.add(job);

        logger.log(String.format(
            "%s added %s to queue.",
            name,
            job
        ));
    }

    @Override
    public Boolean topQueue(int job) {
        if (job >= queue.size()) {
            return false;
        }

        queue.add(0, queue.remove(job));
        return true;
    }

    @Override
    public void clearQueue() {
        queue.clear();
        logger.log(String.format(
            "%s printer queue was cleared.",
            name
        ));
    }

    @Override
    public void print() {
        if (queue.isEmpty()) {
            return;
        }

        String fileToPrint = queue.get(0);

        try (Scanner scanner = new Scanner(new File(fileToPrint))) {
            StringBuilder stringBuilder = new StringBuilder(String.format(
                "Printing '%s'",
                fileToPrint 
            ));

            while (scanner.hasNextLine()) {
                stringBuilder.append("\n" + scanner.nextLine());
            }

            logger.log(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            logger.log(e.toString());
        }
    }
}