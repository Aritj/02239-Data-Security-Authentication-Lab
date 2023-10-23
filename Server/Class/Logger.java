package Server.Class;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Server.Interface.ILogger;

public class Logger implements ILogger {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String ANSI_YELLOW = "\u001B[33m";
    private String ANSI_RED = "\u001B[31m";
    private String ANSI_RESET = "\u001B[0m";

    @Override
    public void log(String message) {
        String logFormat = "\n%s(Logged @ %s) - %s%s\n";

        String formattedLogMessage = String.format(
            logFormat,
            ANSI_YELLOW,
            dtf.format(LocalDateTime.now()),
            message,
            ANSI_RESET
        );

        // Perform logging
        toLog(formattedLogMessage);
    }

    @Override
    public void error(String message) {
        String errorFormat = "\n%s- ERROR - (Logged @ %s) - %s%s\n";

        String formattedLogMessage = String.format(
            errorFormat,
            ANSI_RED,
            dtf.format(LocalDateTime.now()),
            message,
            ANSI_RESET
        );

        toLog(formattedLogMessage);
    }

    private void toLog(String message) {
        System.out.println(message);
    }
}
