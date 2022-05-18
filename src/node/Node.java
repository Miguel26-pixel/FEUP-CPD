package node;

import node.membership.log.Log;

public class Node {
    private final String nodeID;
    private final String mcastIP;
    private final String mcastPort;
    private final String membershipPort;
    private int membership_counter;
    private Log log;

    public Node(String mcastIP, String mcastPort, String nodeID, String membershipPortPort) {
        this.nodeID = nodeID;
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membershipPort = membershipPortPort;
        this.membership_counter = 0;
        this.log = new Log();
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
            return false;
        }

        return true;
    }

    public boolean leave() {
        if (!this.canLeave()) {
            return false;
        }

        return true;
    }

    private boolean canJoin() {
        return this.membership_counter % 2 == 0;
    }

    private boolean canLeave() {
        return this.membership_counter % 2 != 0;
    }
}
