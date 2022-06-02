package node.store.threading;

import message.messages.GetMessageReply;
import message.messages.PutMessageReply;
import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class GetFailedTask extends Thread {
    private final GetMessageReply messageReply;
    private final Socket socket;

    public GetFailedTask(GetMessageReply message, Socket socket) {
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
