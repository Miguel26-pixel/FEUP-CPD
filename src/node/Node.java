package node;

import client.Services;
import message.Message;
import message.messages.*;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import node.store.KeyValueStore;
import utils.UtilsHash;
import utils.UtilsIP;
import utils.UtilsTCP;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private Socket socket;
    private KeyValueStore keyValueStore;
    private Log log;
    private String myHash;
    private ServerSocket server;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort);

        for (String key: this.membershipService.getView().getEntries().keySet()) {
            System.out.println("IP: " + this.membershipService.getView().getEntries().get(key).getAddress()
                    + " Port: " + this.membershipService.getView().getEntries().get(key).getPort());
        }
        this.myHash = UtilsHash.hashSHA256(nodeID);
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort);
        this.log = new Log();

        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort), 0 , InetAddress.getByName(nodeID));
            System.out.println("Server started: waiting for a client ...");
        } catch (IOException e) {
            System.out.println("Server initialization exception: " + e);
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
                        String fileKey = UtilsHash.hashSHA256(file);
                        String nodeHash = getClosestNodeKey(fileKey);
                        if (nodeHash == null) {
                            System.err.println("Successor node not found");
                            reply = new PutMessageReply("failed");
                        } else if (nodeHash.equals(myHash)) {
                            String key = keyValueStore.putNewPair(file);
                            System.out.println("New key: " + key);
                            reply = new PutMessageReply(key);
                        } else {
                            ViewEntry entry = membershipService.getView().getEntries().get(nodeHash);
                            UtilsTCP.sendTCPString(output,redirectMessage(message,entry.getAddress(), entry.getPort()));
                            continue;
                        }
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

    private String getClosestNodeKey(String hash) {
        View view = this.membershipService.getView();
        if (view.getEntries().isEmpty()) { return null; }
        for (String key: view.getEntries().keySet()) {
            if (key.compareTo(hash) > 0) { return key; }
        }
        return view.getEntries().keySet().iterator().next();
    }

    private String redirectMessage(String message, String address, int port) throws IOException {
        Socket socket = new Socket(address, port);
        System.out.println("Redirecting message...");
        OutputStream output = socket.getOutputStream();
        InputStream input = socket.getInputStream();
        UtilsTCP.sendTCPString(output,message);
        String reply = UtilsTCP.readTCPMessage(input);
        socket.close();
        return reply;
    }
}
