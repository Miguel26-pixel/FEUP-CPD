import client.Services;
import node.Node;
import utils.UtilsIP;

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

        if (UtilsIP.isIPValid(args[0])) {
            System.err.println("Invalid multicast IP address");
            System.exit(1);
        }

        if (UtilsIP.isPortValid(args[1])) {
            System.err.println("Invalid multicast IP port");
            System.exit(1);
        }

        if (UtilsIP.isIPValid(args[2])) {
            System.err.println("Invalid node IP address");
            System.exit(1);
        }

        if (UtilsIP.isPortValid(args[3])) {
            System.err.println("Invalid multicast IP port");
            System.exit(1);
        }

        try{
            Integer.parseInt(args[1]);
            Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port");
        }

        Node node = new Node(args[0], args[1], args[2], args[3]);

        try {
            Services stub = (Services) UnicastRemoteObject.exportObject(node, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(args[2], stub);
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }

        node.run();
    }
}
