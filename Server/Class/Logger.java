package Server.Class;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Server.Interface.ILogger;

public class Logger implements ILogger {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void log(String message) {
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";

        String formattedLogMessage = String.format(
            "\n%s(Logged @ %s) - %s%s\n",
            ANSI_YELLOW,
            dtf.format(LocalDateTime.now()),
            message,
            ANSI_RESET
        );

        // Perform logging
        System.out.println(formattedLogMessage);
    }    
}
