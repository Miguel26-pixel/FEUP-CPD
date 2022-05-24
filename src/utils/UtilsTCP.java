package utils;

import message.Message;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;

public class UtilsTCP {
    public static void  sendTCPMessage(Socket socket, Message message) throws IOException {
        OutputStream output = socket.getOutputStream();
        output.write(message.assemble());
    }

    public static String readTCPMessage(Socket socket) {
        StringBuilder message = new StringBuilder();
        try {
            DataInputStream input = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            int n;
            char[] buf = new char[1024];
            while((n = reader.read(buf)) != -1) {
                for (int i = 0; i < n; i++) {
                    message.append(buf[i]);
                }
            }
        } catch (IOException e) {
            System.err.println("TCP Exception: " + e);
        }
        return message.toString();
    }
}
