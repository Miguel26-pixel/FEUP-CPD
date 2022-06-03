package node.comms;

import message.Message;
import message.MessageType;
import node.membership.MembershipService;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPAgent extends CommunicationAgent {
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] delim = new byte[]{CR,LF,CR,LF};

    private final MembershipService membershipService;
    private final KeyValueStore keyValueStore;
    private ServerSocket serverSocket;
    private final int port;
    private final InetAddress address;

    public TCPAgent(MembershipService membershipService, KeyValueStore keyValueStore, String ipAddr, String port) throws IOException {
        super();
        this.membershipService = membershipService;
        this.keyValueStore = keyValueStore;
        this.port = Integer.parseInt(port);
        this.address = InetAddress.getByName(ipAddr);
    }

    @Override
    protected void read() {
        try {
            if (this.stop.get()) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(TIMEOUT);

                UtilsTCP.readTCPMessage(socket.getInputStream());
                System.out.println("Got a message!");
                return;
            }
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(TIMEOUT);

            String messageString = UtilsTCP.readTCPMessage(socket.getInputStream());

            switch (Message.getMessageType(messageString)) {
                case JOIN_REPLY -> {
                    membershipService.processJoinReply(messageString);
                }
                case MEMBERSHIP -> {
                    membershipService.processMembership(messageString);
                    socket.close();
                }
                case LEADERSHIP -> {
                    membershipService.processLeadership(messageString);
                }
                case COUP -> {
                    membershipService.processCoup(messageString);
                }
                case GET -> {
                    System.out.println("GET");
                    keyValueStore.processGet(messageString, socket);
                }
                case PUT -> {
                    System.out.println("PUT");
                    keyValueStore.processPut(messageString, socket);
                }
                case DELETE -> {
                    System.out.println("DELETE");
                    keyValueStore.processDelete(messageString, socket);
                }
                case FORCE_PUT -> {
                    System.out.println("FORCE PUT");
                    keyValueStore.processForcePut(messageString);
                }
                case FORCE_DELETE -> {
                    System.out.println("FORCE DELETE");
                    keyValueStore.processForceDelete(messageString);
                }
                default -> {
                    socket.close();
                    System.err.println("Wrong message header");
                }
            }
        } catch (Exception ignore) {}
    }

    public void sendFilesToLeave() {
        keyValueStore.sendFilesToNextNodes();
    }

    public int getPort() {
        return this.serverSocket.getLocalPort();
    }

    @Override
    public void stopExecution() {
        super.stopExecution();
        try {
            this.serverSocket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void resumeExecution() {
        try {
            this.serverSocket = new ServerSocket(this.port, 0, this.address);
            this.serverSocket.setSoTimeout(TIMEOUT);
            System.out.println("Created socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.resumeExecution();
    }
}
