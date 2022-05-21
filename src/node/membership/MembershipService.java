package node.membership;

import node.membership.log.Log;

import java.net.*;

public class MembershipService {
    private MulticastSocket multicastSocket;
    private final String mcastIP;
    private final String mcastPort;
    private final String membershipPort;
    private int membership_counter;
    private Log log;

    public MembershipService(String mcastIP, String mcastPort, String membershipPort) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membershipPort = membershipPort;
        this.membership_counter = 0;
        this.log = new Log();
    }

    private boolean joinMulticastGroup() {
        int multicastPort = Integer.parseInt(mcastPort);

        try {
            this.multicastSocket = new MulticastSocket(multicastPort);

            multicastSocket.joinGroup(Inet4Address.getByName(mcastIP));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean join() {
        if (!this.canJoin()) {
            membership_counter++;
            return false;
        }

        if (!joinMulticastGroup()) {
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

    //for testing TCP communication
    public boolean sayHello(String nodeIP, String nodePort) {
        return false;
    }
}
