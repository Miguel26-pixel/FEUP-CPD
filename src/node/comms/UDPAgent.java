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
        SocketAddress socketAddress = new InetSocketAddress(this.groupAddress, this.groupPort);

        multicastSocket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(this.groupAddress));

        this.buffer = new byte[DATAGRAM_LENGTH];
    }

    @Override
    protected void read() {
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

        try {
            multicastSocket.receive(receivedPacket);

            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            receivedPacket = new DatagramPacket(buffer, buffer.length, address, port);

            String messageString = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

            switch (Message.getMessageType(messageString)) {
                case JOIN -> {
                    System.out.println("JOIN");
                    this.membershipService.processJoin(messageString);
                }
                case LEAVE -> {
                    System.out.println("LEAVE");
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
