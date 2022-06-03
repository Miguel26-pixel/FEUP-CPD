package node.membership.threading;

import message.messages.PutMessage;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static node.comms.CommunicationAgent.TIMEOUT;

public class SendPutTask extends Thread {
    private final String address;
    private final int port;
    private final PutMessage message;
    private final KeyValueStore keyValueStore;
    private final String fileKey;

    public SendPutTask(String address, int port, PutMessage message, KeyValueStore keyValueStore, String fileKey) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.keyValueStore = keyValueStore;
        this.fileKey = fileKey;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(address, port)) {
            socket.setSoTimeout(TIMEOUT);
            System.out.println("Sending file... ");
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            String state = keyValueStore.deleteValue(fileKey);
            System.out.println("File deleted operations has " + state);
            UtilsTCP.sendTCPMessage(output, message);
            UtilsTCP.readTCPMessage(input);
            System.out.println("File moved with success");

        } catch (IOException e) {
            System.out.println("TCP exception" + e);
        }
    }
}
