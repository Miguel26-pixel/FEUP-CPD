package node;

import client.Services;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.store.KeyValueStore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private KeyValueStore keyValueStore;
    private Log log;
    private ServerSocket server;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort, membershipPort);
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort);
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

    @Override
    public void join() throws RemoteException {
        //TODO
    }

    @Override
    public void leave() throws RemoteException {
        //TODO
    }

    public void run() {
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println("Client accepted");

                input = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String[] parsedMessage = readMessage(reader);
                if (parsedMessage == null || parsedMessage[0] == null || parsedMessage[1] == null) {
                    System.err.println("Read message failed");
                    continue;
                }
                switch (parsedMessage[0]) {
                    case "put":
                        handlePut(socket, parsedMessage[1]);
                        break;
                    case "get":
                        handleGet(socket, parsedMessage[1]);
                        break;
                    case "delete":
                        handleDelete(socket, parsedMessage[1]);
                        break;
                    default:
                        break;
                }

                socket.close();
                input.close();
            } catch (IOException e) {
                System.err.println("Server exception:" + e);
            }
        }
    }

    private String[] readMessage(BufferedReader reader) throws IOException {
        String op = reader.readLine();
        if (op == null) {
            System.err.println("Operation readed from the socket is null");
            return null;
        }
        String arg = reader.readLine();
        if (arg == null) {
            System.err.println("Argument readed from the socket is null");
            return null;
        }
        if (!reader.readLine().equals("END")) {
            System.err.println("End of the message failed");
            return null;
        }
        return new String[]{op, arg};
    }

    private void handlePut(Socket socket, String filename) throws IOException {
        String key = keyValueStore.putNewPair(filename);
        System.out.println("New key = " + key);
        OutputStream output = socket.getOutputStream();
        output.write((key + "\n").getBytes());
        output.write(("END").getBytes());
    }

    private void handleGet(Socket socket, String key) throws IOException {
        File file = keyValueStore.getValue(key);
        OutputStream output = socket.getOutputStream();
        if (file == null) {
            System.out.println("File not found");
            output.write(("failed\n").getBytes());
            output.write(("END").getBytes());
        } else {
            output.write(("succeeded\n").getBytes());
            FileInputStream in = new FileInputStream(file);
            int n;
            while ((n = in.read()) != -1)  {
                output.write(n);
            }
            output.write(("\nEND").getBytes());
        }
    }

    private void handleDelete(Socket socket, String key) throws IOException {
        boolean res = keyValueStore.deleteValue(key);
        System.out.println("Delete operation " + ((res) ? "succeeded" : "failed"));
        OutputStream output = socket.getOutputStream();
        output.write(((res) ? "succeeded\n" : "failed\n").getBytes());
        output.write(("END").getBytes());
    }
}
