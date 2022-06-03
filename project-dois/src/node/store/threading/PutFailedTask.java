package node.store.threading;

import message.messages.GetMessageReply;
import message.messages.PutMessageReply;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class PutFailedTask extends Thread{
    private final PutMessageReply messageReply;
    private final Socket socket;

    public PutFailedTask(PutMessageReply message, Socket socket) {
        this.messageReply = message;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.err.println("Successor node not found");
        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), messageReply);
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
