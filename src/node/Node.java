package node;

import client.Services;
import message.Message;
import node.membership.MembershipService;
import node.membership.log.Log;
import node.store.KeyValueStore;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private ServerSocket server;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort);

        for (String key: this.membershipService.getView().getEntries().keySet()) {
            System.out.println("IP: " + this.membershipService.getView().getEntries().get(key).getAddress()
                    + " Port: " + this.membershipService.getView().getEntries().get(key).getPort());
        }
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort, UtilsHash.hashSHA256(this.nodeID));

        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort), 0 , InetAddress.getByName(this.nodeID));
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
                String reply;

                switch (Message.getMessageType(message)) {
                    case PUT -> reply = keyValueStore.handlePut(message,membershipService.getView());
                    case GET -> reply = keyValueStore.handleGet(message,membershipService.getView());
                    case DELETE -> reply = keyValueStore.handleDelete(message,membershipService.getView());
                    default -> {
                        System.err.println("Wrong message header");
                        continue;
                    }
                }

                System.out.println("Sending reply");
                UtilsTCP.sendTCPString(output, reply);
                socket.close();
            } catch (IOException e) {
                System.err.println("Server exception:" + e);
            }
            System.out.println();
        }
    }
}
