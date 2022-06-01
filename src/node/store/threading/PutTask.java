package node.store.threading;

import message.Message;
import message.messages.PutMessageReply;
import node.membership.view.View;
import node.store.KeyValueStore;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class PutTask extends Thread {
    private final String putMessageString;
    private final Socket socket;
    private final KeyValueStore keyValueStore;

    public PutTask(String putMessageString, Socket socket, KeyValueStore keyValueStore) {
        this.putMessageString = putMessageString;
        this.socket = socket;
        this.keyValueStore = keyValueStore;
    }

    @Override
    public void run() {
        String newKey = keyValueStore.putNewPair(Message.getMessageBody(putMessageString));
        if (newKey == null) {
            System.err.println("Put operation failed");
            newKey = "Put operation failed";
        } else {
            System.out.println("New key: " + newKey);
        }

        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), new PutMessageReply(newKey));
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
