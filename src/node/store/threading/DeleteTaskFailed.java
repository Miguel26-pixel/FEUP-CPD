package node.store.threading;

import message.messages.DeleteMessageReply;
import message.messages.PutMessageReply;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class DeleteTaskFailed extends Thread {
    private final DeleteMessageReply messageReply;
    private final Socket socket;

    public DeleteTaskFailed(DeleteMessageReply message, Socket socket) {
        this.messageReply = message;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.err.println("Successor node not found");
        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), messageReply);
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
