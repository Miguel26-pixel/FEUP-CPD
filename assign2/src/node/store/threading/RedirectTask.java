package node.store.threading;

import utils.UtilsTCP;

import java.io.IOException;
import java.net.Socket;

public class RedirectTask extends Thread {
    private final String message;
    private final String address;
    private final int port;
    private final Socket socket;

    public RedirectTask(Socket socket, String message, String address, int port) {
        this.message = message;
        this.address = address;
        this.port = port;
        this.socket = socket;
    }

    @Override
    public void run() {
        String reply = UtilsTCP.redirectMessage(message, address, port);
        try {
            UtilsTCP.sendTCPString(socket.getOutputStream(), reply);
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }
}
