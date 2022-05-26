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
import java.util.concurrent.*;

public class Node implements Services {
    private final String nodeID;
    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private ServerSocket server;
    private final ThreadPoolExecutor executorTCP;
    private final ThreadPoolExecutor executorUDP;
    private boolean isJoined;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort);
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort, UtilsHash.hashSHA256(this.nodeID));
        this.executorTCP = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.executorUDP = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.isJoined = false;

        try {
            this.server = new ServerSocket(Integer.parseInt(membershipPort), 0 , InetAddress.getByName(this.nodeID));
            System.out.println("Server started: waiting for a client ...");
        } catch (IOException e) {
            System.out.println("Server initialization exception: " + e);
            System.exit(1);
        }

        for (String key: this.membershipService.getView().getEntries().keySet()) {
            System.out.println("IP: " + this.membershipService.getView().getEntries().get(key).getAddress()
                    + " Port: " + this.membershipService.getView().getEntries().get(key).getPort());
        }
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    @Override
    public void join() throws RemoteException {
        //TODO
        this.isJoined = true;
    }

    @Override
    public void leave() throws RemoteException {
        //TODO
        this.isJoined = false;
    }

    public void run() {
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        scheduledExecutor.scheduleAtFixedRate(this::sendMembership, 1, 1, TimeUnit.SECONDS);
        Thread thread = new Thread(this::handleUDPMessages);
        thread.start();
        handleTCPMessage();
    }

    private void handleTCPMessage() {
        System.out.println();
        while (true) {
            try {
                Socket socket = server.accept();
                System.out.println("Client accepted");
                executorTCP.submit(() -> processTCPTask(socket));
            } catch (IOException e) {
                System.err.println("Server exception:" + e);
            }
        }
    }

    private void processTCPTask(Socket socket) {
        try {
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
                    socket.close();
                    return;
                }
            }

            System.out.println("Sending reply");
            UtilsTCP.sendTCPString(output, reply);
            socket.close();
            System.out.println();
        } catch (IOException e) {
            System.err.println("Process Task Exception:" + e);
        }
    }

    private void handleUDPMessages() {
        //TODO
        executorUDP.submit(() -> {
            System.out.println("Waiting for UDP message");
            processUDPTask();
        });
    }

    private void processUDPTask() {
        //TODO
        System.out.println("Processing UDP message");
    }

    private void sendMembership() {
        //TODO
        if (isJoined) {
            System.out.println("Send membership");
        }
    }
}
