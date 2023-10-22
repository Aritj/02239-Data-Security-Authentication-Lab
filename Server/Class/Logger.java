package Server.Class;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Server.Interface.ILogger;

public class Logger implements ILogger {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void log(String message) {
        String formattedLogMessage = String.format(
            "|%s| %s",
            dtf.format(LocalDateTime.now()),
            message
        );

        // Perform logging
        System.out.println(formattedLogMessage);
    }    
}
