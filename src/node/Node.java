package node;

import client.Services;
import node.comms.TCPAgent;
import node.comms.UDPAgent;
import node.membership.MembershipService;
import node.store.KeyValueStore;

import java.io.*;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private KeyValueStore keyValueStore;
    private MembershipService membershipService;
    //private ServerSocket server;
    private final TCPAgent tcpAgent;
    private final UDPAgent udpAgent;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) throws IOException {
        this.nodeID = nodeID;
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort);
        this.membershipService = new MembershipService(nodeID);

        this.tcpAgent = new TCPAgent(membershipService, keyValueStore, nodeID, membershipPort);
        this.udpAgent = new UDPAgent(membershipService, keyValueStore, mcastIP, mcastPort);

        /*
        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort), 0 , InetAddress.getByName(nodeID));
            System.out.println("Server started: waiting for a client ...");
        } catch (IOException e) {
            System.out.println("Server initialization exception: " + e);
            System.exit(1);
        }
        */
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    @Override
    public void join() throws RemoteException {

    }

    @Override
    public void leave() throws RemoteException {

    }

    public void run() {
        this.udpAgent.start();
        this.tcpAgent.run();
    }

    /*
    public void run() {
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println("Client accepted");

                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream();

                String message = UtilsTCP.readTCPMessage(input);
                Message reply;
                switch (Message.getMessageType(message)) {
                    case PUT -> {
                        String file = Message.getMessageBody(message);
                        String key = keyValueStore.putNewPair(file);
                        System.out.println("New key: " + key);
                        reply = new PutMessageReply(key);
                    }
                    case GET -> {
                        GetMessage getMessage = GetMessage.assembleMessage(Message.getMessageBody(message));
                        File file = keyValueStore.getValue(getMessage.getKey());
                        System.out.println((file == null) ? "File does not exists" : "File obtained with success");
                        reply = new GetMessageReply(file);
                    }
                    case DELETE -> {
                        DeleteMessage deleteMessage = DeleteMessage.assembleMessage(Message.getMessageBody(message));
                        String state = keyValueStore.deleteValue(deleteMessage.getKey());
                        System.out.println("Delete operation has " + state);
                        reply = new DeleteMessageReply(state);
                    }
                    default -> {
                        System.err.println("Wrong message header");
                        continue;
                    }
                }
                UtilsTCP.sendTCPMessage(output, reply);
                socket.close();
            } catch (IOException e) {
                System.err.println("Server exception:" + e);
            }
        }
    }

     */
}
