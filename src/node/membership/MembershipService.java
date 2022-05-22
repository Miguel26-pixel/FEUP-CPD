package node.membership;

import node.membership.message.MembershipMessage;
import node.membership.message.Message;
import node.membership.view.View;
import node.membership.message.JoinMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipService {
    private enum MessageReadState {
        START,
        FIRST_FLAG,
        DONE
    }

    private final static int MEMBERSHIP_PORT = 5525;
    private final static int MAX_TRIES = 3;
    private final static int TIMEOUT = 3000;

    private MulticastSocket multicastSocket;
    private final String mcastIP;
    private final String mcastPort;
    private int membership_counter;
    private View view;

    public MembershipService(String mcastIP, String mcastPort) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.membership_counter = 0;
        this.view = new View();
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

            List<Byte> messageBytes = new ArrayList<>();

            for (int current_try = 0; current_try < MAX_TRIES; current_try++) {
                Socket clientSocket = membershipSocket.accept();
                clientSocket.setSoTimeout(TIMEOUT);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                receiveMembershipMessage(reader);
            }
        } catch (Exception e) {
            return false;
        }


        membership_counter++;
        return true;
    }

    private void receiveMembershipMessage(BufferedReader reader) throws IOException {
        MessageReadState messageReadState = MessageReadState.START;
        List<Byte> messageBytes = new ArrayList<>();

        while (messageReadState != MessageReadState.DONE) {
            Byte current = (byte) reader.read();

            if (messageBytes.size() < 2) {
                messageBytes.add(current);
                continue;
            }

            Byte previous = messageBytes.get(messageBytes.size() - 1);

            messageBytes.add(current);

            if (previous.equals(Message.CR) && current.equals(Message.LF)) {
                if (messageReadState == MessageReadState.START) {
                    messageReadState = MessageReadState.FIRST_FLAG;
                } else {
                    messageReadState = MessageReadState.DONE;

                    StringBuilder message = new StringBuilder();
                    for (byte b: messageBytes) {
                        message.append(b);
                    }

                    MembershipMessage membershipMessage = new MembershipMessage(message.toString());

                    this.view.copyView(membershipMessage.getView(), true);
                }
            } else {
                messageReadState = MessageReadState.START;
            }

            if (current.equals((byte) -1)) { //-1 indicates EOF, which is unexpected
                return;
            }
        }
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
