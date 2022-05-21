package node.membership;

import node.membership.view.View;
import node.membership.message.JoinMessage;

import java.io.IOException;
import java.net.*;

public class MembershipService {
    private final static int MEMBERSHIP_PORT = 5525;
    private final static int MAX_TRIES = 3;
    private final static int TIMEOUT = 3000;

    private MulticastSocket multicastSocket;
    private final String mcastIP;
    private final String mcastPort;
    private int membership_counter;
    private View log;

    public MembershipService(String mcastIP, String mcastPort) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membership_counter = 0;
        this.log = new View();
    }

    private boolean joinMulticastGroup() throws IOException {
        int multicastPort = Integer.parseInt(mcastPort);

        this.multicastSocket = new MulticastSocket(multicastPort);

        multicastSocket.joinGroup(Inet4Address.getByName(mcastIP));

        return true;
    }

    public boolean join() {
        if (!this.canJoin()) {
            membership_counter++;
            return false;
        }


        try {
            joinMulticastGroup();

            ServerSocket membershipSocket = new ServerSocket(MEMBERSHIP_PORT);
            membershipSocket.setSoTimeout(TIMEOUT);

            byte[] joinMessage = (new JoinMessage(this.membership_counter, MembershipService.MEMBERSHIP_PORT)).assemble();

            multicastSocket.send(new DatagramPacket(joinMessage, joinMessage.length));

            for (int current_try = 0; current_try < MAX_TRIES; current_try++) {
                // receive membership messages
            }
        } catch (Exception e) {
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
