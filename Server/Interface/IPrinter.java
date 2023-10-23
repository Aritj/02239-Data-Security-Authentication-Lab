package Server.Interface;

import java.util.ArrayList;

public abstract interface IPrinter {
    public String getPrinterName();

    public void setPrinterName(String name);

    public void print();

    public ArrayList<String> getQueue();

    public String getQueueAsString();
    
    public void addToQueue(String job);

    public Boolean topQueue(int job);

    public void clearQueue();
}