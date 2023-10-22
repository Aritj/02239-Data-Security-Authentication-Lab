package Server.Interface;

import java.util.ArrayList;

public abstract interface IPrinter {
    public Boolean status();

    public String getPrinterName();

    public void setPrinterName(String name);

    public void print(String filename);

    public ArrayList<String> getQueue();

    public String getQueueAsString();
    
    public void addToQueue(String job);

    public void topQueue(int job);

    public void clearQueue();
}