package node;

import node.membership.log.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Node {
    private final String nodeID;
    private final String mcastIP;
    private final String mcastPort;
    private final String membershipPort;
    private int membership_counter;
    private Log log;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream out;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPort) {
        this.nodeID = nodeID;
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membershipPort = membershipPort;
        this.membership_counter = 0;
        this.log = new Log();
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
        System.out.println("mcastIP = " + this.mcastIP);
        System.out.println("mcastPort = " + this.mcastPort);
        System.out.println("membershipPort = " + this.membershipPort);
    }

    public boolean join() {
        if (!this.canJoin()) {
            membership_counter++;
            return false;
        }
        membership_counter++;
        return true;
    }

    public boolean leave() {
        if (!this.canLeave()) {
            membership_counter++;
            return false;
        }
        membership_counter++;
        return true;
    }

    private boolean canJoin() {
        return this.membership_counter % 2 == 0;
    }

    private boolean canLeave() {
        return this.membership_counter % 2 != 0;
    }
}
