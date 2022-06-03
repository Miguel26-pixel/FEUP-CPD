package node.comms;

import message.Message;
import message.MessageType;
import node.membership.MembershipService;
import node.store.KeyValueStore;

import java.io.IOException;
import java.net.*;

public class UDPAgent extends CommunicationAgent {
    private final static int DATAGRAM_LENGTH = 1024;

    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private final MulticastSocket multicastSocket;
    private final InetAddress groupAddress;
    private final int groupPort;
    private final byte[] buffer;

    public UDPAgent(MembershipService membershipService, KeyValueStore keyValueStore, String groupAddress, String groupPortString) throws IOException {
        super();

        this.membershipService = membershipService;
        this.keyValueStore = keyValueStore;
        this.groupPort = Integer.parseInt(groupPortString);

        this.multicastSocket = new MulticastSocket(groupPort);
        this.multicastSocket.setSoTimeout(TIMEOUT);

        this.groupAddress = InetAddress.getByName(groupAddress);

        this.buffer = new byte[DATAGRAM_LENGTH];
    }

    public void udpJoinGroup() {
        SocketAddress socketAddress = new InetSocketAddress(this.groupAddress, this.groupPort);
        try {
            multicastSocket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(this.groupAddress));
        } catch (IOException e) {
            System.err.println("Could not join multicast group");
        }
    }

    public void udpLeaveGroup() {
        SocketAddress socketAddress = new InetSocketAddress(this.groupAddress, this.groupPort);
        try {
            multicastSocket.leaveGroup(socketAddress, NetworkInterface.getByInetAddress(this.groupAddress));
        } catch (IOException e) {
            System.err.println("Could not join multicast group");
        }
    }

    @Override
    protected void read() {
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

        try {
            if (this.stop.get()) {
                Thread.sleep(1);
                return;
            }

            multicastSocket.receive(receivedPacket);

            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            receivedPacket = new DatagramPacket(buffer, buffer.length, address, port);

            String messageString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

            switch (Message.getMessageType(messageString)) {
                case JOIN -> {
                    this.membershipService.processJoin(messageString, keyValueStore, this.membershipService.getIdentifier());
                }
                case LEAVE -> {
                    this.membershipService.processLeave(messageString);
                }
            }
        } catch (Exception ignore) {}
    }

    public void send(Message message) throws IOException {
        MessageType messageType = message.getMessageType();

        if (messageType == MessageType.JOIN || messageType == MessageType.LEAVE) {
            byte[] messageBytes = message.assemble();

            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, this.groupAddress, this.groupPort);

            multicastSocket.send(packet);
        }
    }
}
