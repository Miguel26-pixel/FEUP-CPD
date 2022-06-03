package utils;

import message.Message;
import message.header.FieldType;
import message.header.MessageField;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static node.comms.CommunicationAgent.TIMEOUT;

public class UtilsTCP {
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] delim = new byte[]{CR,LF,CR,LF};
    public static void  sendTCPMessage(OutputStream output, Message message) throws IOException {
        try {
            if (message != null) {
                output.write(message.assemble());
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readTCPMessage(InputStream socketInput) {
        StringBuilder message = new StringBuilder();
        try {
            DataInputStream input = new DataInputStream(
                    new BufferedInputStream(socketInput));

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            int n, bodyLenght;
            boolean receiving_header = true;
            char[] buf = new char[1024];
            while((n = reader.read(buf)) != -1) {
                int i;
                for (i = 0; i < n; i++) {
                    message.append(buf[i]);
                    if (message.length() > 3 && message.substring(message.length() - 4).equals(new String(delim))) {
                        i++;
                        receiving_header = false;
                        break;
                    }
                }

                if (receiving_header) { continue; }

                bodyLenght = getBodyLenght(message.toString());
                if (bodyLenght == -1) {
                    System.err.println("Body lenght not specified");
                    return message.toString();
                }

                for (; i < n; i++) {
                    message.append(buf[i]);
                    bodyLenght--;
                    if (bodyLenght == 0) { break; }
                }
                if (bodyLenght == 0) { break; }
            }
        } catch (IOException e) {
            System.err.println("TCP Exception: " + e);
        }
        return message.toString();
    }

    private static int getBodyLenght(String header) {
        byte[] header_delim = new byte[]{CR,LF};
        List<String> split = new ArrayList<>(List.of(header.split(new String(header_delim))));

        for (String s : split) {
            String[] field = s.split(" ");
            if (MessageField.translateFieldHeader(field[0]) == FieldType.BODYLENGHT) {
                return Integer.parseInt(field[1]);
            }
        }
        return -1;
    }

    public static void sendTCPString(OutputStream output, String message) {
        try {
            output.write(message.getBytes());
            output.flush();
        } catch (IOException e) {
            System.err.println("TCP Send String Exception: " + e);
        }
    }

    public static String redirectMessage(String message, String address, int port) {
        String reply = "";
        try (Socket socket = new Socket(address, port)) {
            socket.setSoTimeout(TIMEOUT);
            System.out.println("Redirecting message...");
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            UtilsTCP.sendTCPString(output,message);
            reply = UtilsTCP.readTCPMessage(input);
        } catch (IOException e) {
            System.err.println("TCP Redirect Exception: " + e);
        }
        return reply;
    }
}
