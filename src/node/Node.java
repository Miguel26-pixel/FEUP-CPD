package node;

import client.Services;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.store.KeyValueStore;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private KeyValueStore keyValueStore;
    private Log log;
    private ServerSocket server;
    //private Socket socket;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort, membershipPort);
        this.keyValueStore = new KeyValueStore(nodeID);
        this.log = new Log();
        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort));
            //this.socket = new Socket(nodeID, Integer.parseInt(membershipPort));
            System.out.println("Server started");
            System.out.println("Socket waiting for a client ...");
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    public void run() {
        while (true) {

            try {
                Socket socket = server.accept();
                System.out.println("Client accepted");

                input = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));

                String line = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    line = reader.readLine();
                    if (line != null && line.equals("leave")) {
                        break;
                    }
                    String[] words = line.split(";");
                    System.out.println(words[0]);
                    switch (words[0]) {
                        case "put":
                            System.out.println(keyValueStore.putNewPair(words[1]));
                            break;
                        case "get":
                            System.out.println(keyValueStore.getValue(words[1]));
                            break;
                        case "delete":
                            System.out.println(keyValueStore.deleteValue(words[1]));
                            break;
                        default:
                            break;
                    }
                    System.out.println("Closing connection");

                    socket.close();
                    input.close();
                } catch (IOException i) {
                    System.out.println(i);
                }
            } catch (IOException e) {
                System.err.println("Server exception:" + e);
            }
        }
    }
    /*@Override
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
    }*/
}
