package node;

import node.membership.MembershipService;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Node {
    private final String nodeID;
    private MembershipService membershipService;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream out;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.membershipService = new MembershipService(mcastIP, mcastPort);
        try {
            this.socket = new Socket(nodeID, Integer.parseInt(membershipPort));
            this.input = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void start() {
        System.out.println("start");
        System.out.println("nodeID = " + this.nodeID);
    }
}
