package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Services extends Remote {
    String sayHello() throws RemoteException;
}
