package node.store.threading;

import message.Message;
import message.messages.ForcePutMessage;
import message.messages.PutMessageReply;
import node.store.KeyValueStore;
import utils.UtilsFile;
import utils.UtilsTCP;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

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
        String file = Message.getMessageBody(putMessageString);
        String newKey = keyValueStore.putNewPair(file);
        if (newKey == null) {
            System.err.println("Put operation failed");
            newKey = "Put operation failed";
        } else {
            System.out.println("New key: " + newKey);
            List<Socket> socketList = keyValueStore.getNextTwoActiveNodes();
            File temp = UtilsFile.stringToFile(file, keyValueStore.getDirPath() + "temp");
            ForcePutMessage message = new ForcePutMessage(temp);
            for (Socket s: socketList) {
                System.out.println("Trying to replicate file...");
                try {
                    UtilsTCP.sendTCPMessage(s.getOutputStream(), message);
                    s.close();
                } catch (IOException e) {
                    System.err.println("Cannot replicate file");
                }
            }
            temp.delete();
        }

        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), new PutMessageReply(newKey));
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
