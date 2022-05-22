package node;

import client.Services;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.store.KeyValueStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private KeyValueStore keyValueStore;
    private Log log;
    private ServerSocket server;
    private Socket socket;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort, membershipPort);
        this.keyValueStore = new KeyValueStore(nodeID);
        this.log = new Log();
        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort));
            this.socket = new Socket(nodeID, Integer.parseInt(membershipPort));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    @Override
    public String get(String key) throws RemoteException {
        return keyValueStore.getValue(key);
    }

    @Override
    public String put(String filepath) throws RemoteException {
        return keyValueStore.putNewPair(filepath);
    }

    @Override
    public String delete(String key) throws RemoteException {
        if (keyValueStore.deleteValue(key)) {
            System.out.println("Pair with key = " + key + " was deleted with success");
            return "Pair with key = " + key + " was deleted with success";
        }
        System.err.println("Pair with key = " + key + " could not be deleted");
        return "Pair with key = " + key + " could not be deleted";
    }
}
