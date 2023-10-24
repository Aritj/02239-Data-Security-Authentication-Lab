package Server.Class;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import Server.Interface.ILogger;
import Server.Interface.IPrinter;

class Printer implements IPrinter {
    private ArrayList<String> queue = new ArrayList<String>();
    private ScheduledExecutorService ses;
    private ILogger logger;
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
    }

    @Override
    public void print() {
        if (queue.isEmpty()) {
            return;
        }

        String filename = queue.remove(0);
        String fileContents = read(filename);

        if (fileContents == null) {
            logger.error(String.format(
                "%s printer could not find '%s'.",
                name,
                filename
            ));
            return;
        }

        logger.log(String.format(
            "%s printer finished printing '%s':\n%s",
            name,
            filename,
            fileContents
        ));
    }

    private String read(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            StringBuilder stringBuilder = new StringBuilder();

            while (scanner.hasNextLine()) {
                stringBuilder.append("\n" + scanner.nextLine());
            }

            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public void start() {
        ses = Executors.newSingleThreadScheduledExecutor();
        final int printTimer = 3 + ThreadLocalRandom.current().nextInt(5); // random between 3-8s

        ses.scheduleAtFixedRate(this::print, printTimer, printTimer, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        ses.shutdownNow();
    }
}