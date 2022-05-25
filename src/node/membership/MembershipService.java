package node.membership;

import message.messages.JoinMessage;
import message.messages.LeaveMessage;
import message.messages.MembershipMessage;
import node.membership.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipService extends Thread {
    private final static int MEMBERSHIP_PORT = 5525;
    private final static int MAX_TRIES = 3;
    private final static int TIMEOUT = 3000;
    private final static int DATAGRAM_LENGTH = 1024;

    private MulticastSocket multicastSocket;
    private final String mcastIP;
    private final String mcastPort;
    private final String nodeIP;
    private int membership_counter;
    private final View view;

    public MembershipService(String mcastIP, String mcastPort, String nodeIP) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.nodeIP = nodeIP;
        this.membership_counter = 0;
        this.view = new View();
    }

    @Override
    public void run() {
    }

    private boolean joinMulticastGroup() throws IOException {
        int multicastPort = Integer.parseInt(mcastPort);

        this.multicastSocket = new MulticastSocket(multicastPort);

        multicastSocket.joinGroup(Inet4Address.getByName(mcastIP));

        return true;
    }

    public boolean joinCluster() {
        if (!this.canJoin()) {
            return false;
        }

        try {
            joinMulticastGroup();

            ServerSocket membershipSocket = new ServerSocket(MEMBERSHIP_PORT);
            membershipSocket.setSoTimeout(TIMEOUT);

            byte[] joinMessage = (new JoinMessage(this.membership_counter, MembershipService.MEMBERSHIP_PORT, this.nodeIP)).assemble();

            multicastSocket.send(new DatagramPacket(joinMessage, joinMessage.length));

            for (int current_try = 0; current_try < MAX_TRIES; current_try++) {
                Socket clientSocket = membershipSocket.accept();
                clientSocket.setSoTimeout(TIMEOUT);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                receiveMembershipMessage(reader);
                clientSocket.close();
            }

            membershipSocket.close();
        } catch (Exception e) {
            return false;
        }

        membership_counter++;
        return true;
    }

    private void receiveMembershipMessage(BufferedReader reader) throws IOException {
        List<Byte> messageBytes = new ArrayList<>();

        while (true) {
            Byte current = (byte) reader.read();
            messageBytes.add(current);

            if (current.equals((byte) -1)) {

                StringBuilder message = new StringBuilder();
                for (byte b: messageBytes) {
                    message.append(b);
                }

                MembershipMessage membershipMessage = new MembershipMessage(message.toString());

                this.view.copyView(membershipMessage.getView(), true);

                break;
            }
        }
    }

    public boolean leaveCluster() {
        if (!this.canLeave()) {
            return false;
        }

        try {
            byte[] leaveMessage = (new LeaveMessage(this.membership_counter, this.nodeIP)).assemble();

            multicastSocket.send(new DatagramPacket(leaveMessage, leaveMessage.length));
        } catch (IOException ignored) {
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
