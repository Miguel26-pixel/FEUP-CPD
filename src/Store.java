import client.Services;
import node.Node;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Store {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("usage: java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>");
            System.exit(1);
        }

        Node node = new Node(args[0], args[1], args[2], args[3]);

        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Hello", node);

            System.out.println("Bound with gotten registry");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
