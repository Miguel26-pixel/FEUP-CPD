package node.membership;

import message.Message;
import message.messages.JoinMessage;
import message.messages.LeaveMessage;
import message.messages.MembershipMessage;
import node.membership.threading.JoinTask;
import node.membership.view.View;
import utils.UtilsTCP;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private final ThreadPoolExecutor workerThreads;
    private final byte[] buffer;

    public MembershipService(String mcastIP, String mcastPort, String nodeIP) {
        this.mcastIP = mcastIP;
        this.mcastPort = mcastPort;
        this.nodeIP = nodeIP;
        this.membership_counter = 0;
        this.view = new View();

        int numberOfCores = Runtime.getRuntime().availableProcessors();

        this.workerThreads = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfCores);

        this.buffer = new byte[DATAGRAM_LENGTH];
    }

    public View getView() {
        return view;
    }

    @Override
    public void run() {
        System.out.println("Joining membership...");

        if (!this.joinCluster()) {
            return;
        }

        System.out.println("Joined membership");

        while (true) {
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            try {
                multicastSocket.receive(receivedPacket);

                InetAddress address = receivedPacket.getAddress();
                int port = receivedPacket.getPort();
                receivedPacket = new DatagramPacket(buffer, buffer.length, address, port);

                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                switch (Message.getMessageType(message)) {
                    case JOIN ->  {
                        System.out.println("JOIN");
                        workerThreads.execute(new JoinTask(this.view, message));
                    }
                }
            } catch (IOException ignored) {
            }

        }
    }

    private void joinMulticastGroup() throws IOException {
        int multicastPort = Integer.parseInt(mcastPort);

        this.multicastSocket = new MulticastSocket(multicastPort);

        InetAddress ipAddress = InetAddress.getByName(mcastIP);
        SocketAddress socketAddress = new InetSocketAddress(mcastIP, multicastPort);

        multicastSocket.joinGroup(socketAddress, NetworkInterface.getByInetAddress(ipAddress));
    }

    public boolean joinCluster() {
        if (!this.canJoin()) {
            return false;
        }

        try {
            this.joinMulticastGroup();

            ServerSocket membershipSocket = new ServerSocket(MEMBERSHIP_PORT);
            membershipSocket.setSoTimeout(TIMEOUT);

            byte[] joinMessage = (new JoinMessage(this.membership_counter, MembershipService.MEMBERSHIP_PORT, this.nodeIP)).assemble();

            multicastSocket.send(new DatagramPacket(joinMessage, joinMessage.length, InetAddress.getByName(mcastIP), Integer.parseInt(mcastPort)));

            for (int current_try = 0; current_try < MAX_TRIES; current_try++) {
                try {
                    Socket clientSocket = membershipSocket.accept();

                    clientSocket.setSoTimeout(TIMEOUT);

                    String msg = UtilsTCP.readTCPMessage(clientSocket.getInputStream());

                    this.view.copyView(new MembershipMessage(msg).getView(), true);
                    clientSocket.close();
                } catch (SocketTimeoutException ignored) {} // In case of timeout, do nothing
            }

            membershipSocket.close();
        } catch (Exception e) {
            return false;
        }

        membership_counter++;
        return true;
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
}
