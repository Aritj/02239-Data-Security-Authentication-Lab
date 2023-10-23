package Server.Interface;

public interface ILogger {
    /**
     * Logs a message.
     * @param message
     */
    public void log(String message);

    /**
     * Logs an error message.
     * @param message
     */
    public void error(String message);    
}
