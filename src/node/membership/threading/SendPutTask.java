package node.membership.threading;

import message.Message;
import message.messages.PutMessage;
import message.messages.PutMessageReply;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static node.comms.CommunicationAgent.TIMEOUT;

public class SendPutTask extends Thread {
    private final String address;
    private final int port;
    private final PutMessage message;
    private final KeyValueStore keyValueStore;
    private final String filepath;

    public SendPutTask(String address, int port, PutMessage message, KeyValueStore keyValueStore, String filepath) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.keyValueStore = keyValueStore;
        this.filepath = filepath;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(address, port)) {
            socket.setSoTimeout(TIMEOUT);
            System.out.println("Sending file...");
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            UtilsTCP.sendTCPMessage(output, message);
            UtilsTCP.readTCPMessage(input);
            System.out.println("File moved with success");
            String state = keyValueStore.deleteValue(filepath);
            System.out.println("File deleted operations has " + state);
        } catch (IOException e) {
            System.out.println("TCP exception" + e);
        }
    }
}
