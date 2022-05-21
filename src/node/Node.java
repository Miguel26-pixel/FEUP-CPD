package node;

import client.Services;
import node.membership.MembershipService;
import node.membership.log.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Node implements Services {
    private final String nodeID;
    private MembershipService membershipService;
    private Log log;
    private ServerSocket server;
    private Socket socket;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort, membershipPort);
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
}
