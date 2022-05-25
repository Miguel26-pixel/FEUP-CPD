import client.Services;
import node.Node;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer {
    public static boolean register(Node node, String register) {
        try {
            Services stub = (Services) UnicastRemoteObject.exportObject(node, 0);
            Registry registry;
            try {
                LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                System.out.println("RMI registry already lauched");
            }

            registry = LocateRegistry.getRegistry();
            registry.rebind(register, stub);
        } catch (RemoteException e) {
            System.err.println("RMI Exception: " + e);
            return false;
        }
        return true;
    }
}
