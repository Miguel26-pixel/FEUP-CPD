package node;

import client.Services;
import node.comms.TCPAgent;
import node.comms.UDPAgent;
import node.membership.MembershipService;
import node.store.KeyValueStore;
import threading.ThreadPool;
import utils.UtilsHash;

import java.io.*;
import java.rmi.RemoteException;

public class Node implements Services {
    private final String nodeID;
    private final KeyValueStore keyValueStore;
    private final MembershipService membershipService;
    private final ThreadPool workers;
    private final TCPAgent tcpAgent;
    private final UDPAgent udpAgent;
    private final String folderPath;
    private final String folderName;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) throws IOException {
        this.nodeID = nodeID;
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        this.workers = new ThreadPool(numberOfCores, numberOfCores);
        this.folderName = "node_" + nodeID + ":" + membershipPort + "/";
        this.folderPath = "/dynamo/node_" + nodeID + ":" + membershipPort + "/";
        this.membershipService = new MembershipService(nodeID, this.workers);
        this.keyValueStore = new KeyValueStore(this.folderName,
                UtilsHash.hashSHA256(this.nodeID), this.workers, this.membershipService.getView());

        this.tcpAgent = new TCPAgent(this.membershipService, this.keyValueStore, nodeID, membershipPort);
        this.udpAgent = new UDPAgent(this.membershipService, this.keyValueStore, mcastIP, mcastPort);

        if (!this.createDirectory()) {
            throw new IOException("Unable to ensure proper filesystem.");
        }
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    @Override
    public void join() throws RemoteException {
        this.udpAgent.udpJoinGroup();
        this.udpAgent.resumeExecution();
        this.tcpAgent.resumeExecution();
        this.membershipService.join(this.udpAgent, this.tcpAgent.getPort());
        this.keyValueStore.checkPastFiles();
    }

    @Override
    public void leave() throws RemoteException {
        this.udpAgent.udpLeaveGroup();
        this.tcpAgent.stopExecution();
        this.tcpAgent.sendFilesToLeave();
        this.udpAgent.stopExecution();
        this.membershipService.leave(this.udpAgent);
        this.workers.waitForTasks();
    }

    public void run() {
        this.udpAgent.start();
        this.tcpAgent.run();
    }

    private boolean createDirectory() {
        File dynamoDir = new File(folderPath);
        if (!dynamoDir.exists() || !dynamoDir.isDirectory()) {
            boolean res = dynamoDir.mkdir();
            if(res) {
                System.out.println("Dynamo main folder created with success");
            } else {
                System.err.println("Dynamo main folder could not be created");
                return false;
            }
        }

        File nodeDir = new File(folderPath + folderName);
        if (!nodeDir.exists() || !nodeDir.isDirectory()) {
            boolean res = nodeDir.mkdir();
            if(res) {
                System.out.println("Node folder created with success");
            } else {
                System.err.println("Node folder could not be created");
                return false;
            }
        }

        return true;
    }
}
