package node.comms;

import message.Message;
import message.messages.JoinMessage;
import message.messages.MembershipMessage;
import node.membership.MembershipService;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.*;

public class UDPReceiver extends CommunicationReceiver {
    private final static int DATAGRAM_LENGTH = 1024;

    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private final MulticastSocket multicastSocket;
    private final byte[] buffer;

    public UDPReceiver(MembershipService membershipService, KeyValueStore keyValueStore, String groupAddress, String groupPortString) throws IOException {
        super();

        this.membershipService = membershipService;
        this.keyValueStore = keyValueStore;
        int groupPort = Integer.parseInt(groupPortString);

        this.multicastSocket = new MulticastSocket(groupPort);
        this.multicastSocket.setSoTimeout(TIMEOUT);

        InetAddress ipAddress = InetAddress.getByName(groupAddress);
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, groupPort);

        multicastSocket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(ipAddress));

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
                    this.membershipService.processJoin(messageString);
                }
            }
        } catch (Exception ignore) {}
    }
}
