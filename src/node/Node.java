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

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) throws IOException {
        this.nodeID = nodeID;
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        this.workers = new ThreadPool(numberOfCores, numberOfCores);
        this.membershipService = new MembershipService(nodeID, this.workers);
        this.keyValueStore = new KeyValueStore("node_" + nodeID + ":" + membershipPort,
                UtilsHash.hashSHA256(this.nodeID), this.workers, this.membershipService.getView());

        this.tcpAgent = new TCPAgent(this.membershipService, this.keyValueStore, nodeID, membershipPort);
        this.udpAgent = new UDPAgent(this.membershipService, this.keyValueStore, mcastIP, mcastPort);
    }

    @Override
    public String sayHello() {
        return "Hello, world!";
    }

    @Override
    public void join() throws RemoteException {
        this.udpAgent.resumeExecution();
        this.tcpAgent.resumeExecution();
        this.membershipService.join(this.udpAgent, this.tcpAgent.getPort());
    }

    @Override
    public void leave() throws RemoteException {
        this.tcpAgent.stopExecution();
        this.udpAgent.stopExecution();
        this.membershipService.leave(this.udpAgent);
        this.workers.waitForTasks();
    }

    public void run() {
        this.udpAgent.start();
        this.tcpAgent.run();
    }
}
