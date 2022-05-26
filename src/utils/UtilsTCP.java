package utils;

import message.Message;

import java.io.*;
import java.net.Socket;

public class UtilsTCP {
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] delim = new byte[]{CR,LF,CR,LF};
    public static void sendTCPMessage(OutputStream output, Message message) {
        try {
            output.write(message.assemble());
            output.flush();
        } catch (IOException e) {
            System.err.println("TCP Send Message Exception: " + e);
        }
    }

    public static void sendTCPString(OutputStream output, String message) {
        try {
            output.write(message.getBytes());
            output.flush();
        } catch (IOException e) {
            System.err.println("TCP Send String Exception: " + e);
        }
    }

    public static String readTCPMessage(InputStream socketInput) {
        StringBuilder message = new StringBuilder();
        try {
            DataInputStream input = new DataInputStream(
                    new BufferedInputStream(socketInput));

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            int n, counter = 0;
            char[] buf = new char[1024];
            while((n = reader.read(buf)) != -1) {
                for (int i = 0; i < n; i++) {
                    message.append(buf[i]);
                    if (message.length() > 3 && message.substring(message.length() - 4).equals(new String(delim))) {
                        counter++;
                        if (counter == 2) { break; }
                    }
                }
                if (counter == 2) { break; }
            }
        } catch (IOException e) {
            System.err.println("TCP Read Exception: " + e);
        }
        return message.toString();
    }

    public static String redirectMessage(String message, String address, String port) {
        String reply = "";
        try {
            Socket socket = new Socket(address, Integer.parseInt(port));
            System.out.println("Redirecting message...");
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            UtilsTCP.sendTCPString(output,message);
            reply = UtilsTCP.readTCPMessage(input);
            socket.close();
        } catch (IOException e) {
            System.err.println("TCP Redirect Exception: " + e);
        }
        return reply;
    }
}
