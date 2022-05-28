package node.comms;

import message.Message;
import node.membership.MembershipService;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver extends CommunicationReceiver {
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] delim = new byte[]{CR,LF,CR,LF};

    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private final ServerSocket serverSocket;

    public TCPReceiver(MembershipService membershipService, KeyValueStore keyValueStore, String ipAddr, String port) throws IOException {
        super();
        this.membershipService = membershipService;
        this.keyValueStore = keyValueStore;

        this.serverSocket = new ServerSocket(Integer.parseInt(port), 0, InetAddress.getByName(ipAddr));
        this.serverSocket.setSoTimeout(TIMEOUT);
    }

    @Override
    protected void read() {
        try {
            Socket clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(TIMEOUT);

            String messageString = UtilsTCP.readTCPMessage(clientSocket.getInputStream());

            switch (Message.getMessageType(messageString)) {
                case MEMBERSHIP -> {
                    System.out.println("MEMBERSHIP");
                    membershipService.processMembership(messageString);
                }
                case GET -> {
                    System.out.println("GET");
                    // TODO
                }
                case PUT -> {
                    System.out.println("PUT");
                    // TODO
                }
                case DELETE -> {
                    System.out.println("DELETE");
                    // TODO
                }
                case GET_REPLY -> {
                    System.out.println("GET_REPLY");
                    // TODO
                }case PUT_REPLY -> {
                    System.out.println("PUT_REPLY");
                    // TODO
                }case DELETE_REPLY -> {
                    System.out.println("DELETE_REPLY");
                    // TODO
                }
            }
        } catch (Exception ignore) {}
    }
}
