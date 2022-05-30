package node.store.threading;

import message.Message;
import message.messages.PutMessageReply;
import node.membership.view.View;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class PutTask extends Thread {
    private final String putMessageString;
    private final String folderPath;
    private final String folderName;
    private final ArrayList<String> idStore;
    private final Socket socket;

    public PutTask(String putMessageString, Socket socket, String folderPath, String folderName, ArrayList<String> idStore) {
        this.putMessageString = putMessageString;
        this.socket = socket;
        this.folderPath = folderPath;
        this.folderName = folderName;
        this.idStore = idStore;
    }

    @Override
    public void run() {
        String newKey = putNewPair(Message.getMessageBody(putMessageString));
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

    private String putNewPair(String file) {
        String valueKey = UtilsHash.hashSHA256(file);

        File dynamoDir = new File(folderPath);
        if (!dynamoDir.exists() || !dynamoDir.isDirectory()) {
            boolean res = dynamoDir.mkdir();
            if(res) {
                System.out.println("Dynamo main folder created with success");
            } else {
                System.err.println("Dynamo main folder could not be created");
                return null;
            }
        }

        File nodeDir = new File(folderPath + folderName);
        if (!nodeDir.exists() || !nodeDir.isDirectory()) {
            boolean res = nodeDir.mkdir();
            if(res) {
                System.out.println("Node folder created with success");
            } else {
                System.err.println("Node folder could not be created");
                return null;
            }
        }

        File newFile = new File(folderPath + folderName + "/file_" + valueKey);

        try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        idStore.add(valueKey);

        return valueKey;
    }
}
