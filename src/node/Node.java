package node;

import client.Services;
import message.Message;
import message.messages.*;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private Socket socket;
    private KeyValueStore keyValueStore;
    private Log log;
    private ServerSocket server;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort);
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort);
        this.log = new Log();

        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort));
            System.out.println("Server started: waiting for a client ...");
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
}
