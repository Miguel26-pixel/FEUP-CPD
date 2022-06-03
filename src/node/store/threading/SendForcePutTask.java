package node.store.threading;

import message.messages.ForcePutMessage;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static node.comms.CommunicationAgent.TIMEOUT;

public class SendForcePutTask {
    private final String address;
    private final int port;
    private final ForcePutMessage message;

    public SendForcePutTask(String address, int port, ForcePutMessage message) {
        this.address = address;
        this.port = port;
        this.message = message;
    }

    public void sendForcePut() {
        try (Socket socket = new Socket(address, port)) {
            socket.setSoTimeout(TIMEOUT);
            System.out.println("Force sending file...");
            OutputStream output = socket.getOutputStream();
            UtilsTCP.sendTCPMessage(output, message);
            System.out.println("File sended with success");
        } catch (IOException e) {
            System.out.println("TCP exception" + e);
        }
    }
}

