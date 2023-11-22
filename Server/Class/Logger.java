package Server.Class;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Server.Interface.ILogger;

public class Logger implements ILogger {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");

    public Logger() {
        clearLog();
    }

    private void clearLog() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("printserver.log"))) {
            // This will create/overwrite the file with no content, effectively clearing it.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String message) {
        String logFormat = "%s - %s\n";

        String formattedLogMessage = String.format(
            logFormat,
            dtf.format(LocalDateTime.now()),
            message
        );

        toLog(formattedLogMessage);
    }

    @Override
    public void error(String message) {
        String errorFormat = "%s - ERROR: %s\n";

        String formattedLogMessage = String.format(
            errorFormat,
            dtf.format(LocalDateTime.now()),
            message
        );

        toLog(formattedLogMessage);
    }

    private void toLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("printserver.log", true))) {
            writer.append(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
