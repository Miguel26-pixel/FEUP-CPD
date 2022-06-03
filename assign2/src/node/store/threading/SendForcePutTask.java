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
    private final KeyValueStore keyValueStore;
    private final String fileKey;

    public SendForcePutTask(String address, int port, ForcePutMessage message, KeyValueStore keyValueStore, String fileKey) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.keyValueStore = keyValueStore;
        this.fileKey = fileKey;
    }

    public void sendForcePut() {
        try (Socket socket = new Socket(address, port)) {
            socket.setSoTimeout(TIMEOUT);
            System.out.println("Sending file...");
            OutputStream output = socket.getOutputStream();
            UtilsTCP.sendTCPMessage(output, message);
            System.out.println("File sended with success");
            System.out.println(fileKey);
            String state = keyValueStore.deleteValue(fileKey);
            System.out.println("File deleted operations has " + state);
        } catch (IOException e) {
            System.out.println("TCP exception" + e);
        }
    }
}

