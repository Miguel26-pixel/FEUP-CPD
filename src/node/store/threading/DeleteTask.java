package node.store.threading;

import message.Message;
import message.messages.DeleteMessageReply;
import message.messages.ForceDeleteMessage;
import message.messages.GetMessage;
import message.messages.PutMessageReply;
import node.store.KeyValueStore;
import utils.UtilsTCP;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DeleteTask extends Thread {
    private final String deleteMessageString;
    private final KeyValueStore keyValueStore;
    private final Socket socket;

    public DeleteTask(String deleteMessageString, Socket socket, KeyValueStore keyValueStore) {
        this.deleteMessageString = deleteMessageString;
        this.socket = socket;
        this.keyValueStore = keyValueStore;
    }

    @Override
    public void run() {
        GetMessage deleteMessage = GetMessage.assembleMessage(Message.getMessageBody(deleteMessageString));
        String state = keyValueStore.deleteValue(deleteMessage.getKey());
        System.out.println("Delete operation has " + state);

        List<Socket> socketList = keyValueStore.getNextTwoActiveNodes();
        System.out.println("nao passei two active nodes");
        ForceDeleteMessage message = new ForceDeleteMessage(deleteMessage.getKey());
        for (Socket s: socketList) {
            System.out.println("Trying to replicate deletion operation of file...");
            try {
                UtilsTCP.sendTCPMessage(s.getOutputStream(), message);
                s.close();
            } catch (IOException e) {
                System.err.println("Cannot replicate deletion operation");
            }
        }
        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), new DeleteMessageReply(state));
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
