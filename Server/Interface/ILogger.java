package Server.Interface;

public interface ILogger {
    /**
     * It is sufficient that the print server records the invocation of a particular operation in a logfile or prints it on the console.
     * @param message
     */
    public void log(String message);
}
