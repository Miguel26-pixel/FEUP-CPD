package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Services extends Remote {
    String sayHello() throws RemoteException;

    String get(String key) throws RemoteException;

    String put(String filepath) throws RemoteException;

    void delete(String key) throws RemoteException;
}
