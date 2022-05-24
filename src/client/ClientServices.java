package client;

import message.Message;
import message.MessageType;
import message.messages.*;
import utils.UtilsTCP;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientServices {

    private final String nodeArg;
    private final String operation;
    private final String nodeIP;
    private final String operand;
    public ClientServices(String[] args) {
        String[] words = args[0].split(":");
        this.nodeIP = words[0];
        this.nodeArg = words[1];
        this.operation = args[1];
        this.operand = (args.length == 3) ? args[2] : null;
    }

    public void sendRMIJoin() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(nodeIP);
        Services stub = (Services) registry.lookup(nodeArg);
        stub.join();
    }

    public void sendRMILeave() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(nodeIP);
        Services stub = (Services) registry.lookup(nodeArg);
        stub.leave();
    }

    public void handleTCPPut() {
        int port = Integer.parseInt(nodeArg);
        try {
            File file = new File(operand);
            if (!file.exists() || !file.isFile()) {
                System.err.println("File does not exists");
                return;
            }
            Socket socket = new Socket(nodeIP, port);
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            PutMessage message = new PutMessage(file);
            UtilsTCP.sendTCPMessage(output, message);
            String reply = UtilsTCP.readTCPMessage(input);

            if (Message.getMessageType(reply) != MessageType.PUT_REPLY) {
                System.err.println("Message reply incorrect type. Expected: " + MessageType.PUT_REPLY + ". Given: " + Message.getMessageType(reply));
                return;
            }

            PutMessageReply replyMessage = new PutMessageReply(Message.getMessageBody(reply));
            System.out.println("New key: " + replyMessage.getKey());

        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    public void handleTCPGet() {
        int port = Integer.parseInt(nodeArg);
        try {
            Socket socket = new Socket(nodeIP, port);
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            GetMessage message = new GetMessage(operand);
            UtilsTCP.sendTCPMessage(output, message);

            /*File testDir = new File("../clientFiles/");
            if (!testDir.exists() || !testDir.isDirectory()) {
                boolean res = testDir.mkdir();
                if(res) {
                    System.out.println("Client files folder created with success");
                } else {
                    System.err.println("Client files folder could not be created");
                    return;
                }
            }

            File file = readTCPFile(socket, "../clientFiles/file_" + operand);
            if (file == null) {
                System.out.println("File not found");
            } else {
                System.out.println("File retrieved with success (saved in clientFiles as file_" + operand);
            }*/
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    public void handleTCPDelete() {
        int port = Integer.parseInt(nodeArg);
        try {
            Socket socket = new Socket(nodeIP, port);
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            DeleteMessage message = new DeleteMessage(operand);
            UtilsTCP.sendTCPMessage(output, message);
            String reply = UtilsTCP.readTCPMessage(input);

            if (Message.getMessageType(reply) != MessageType.DELETE_REPLY) {
                System.err.println("Message reply incorrect type. Expected: " + MessageType.DELETE_REPLY + ". Given: " + Message.getMessageType(reply));
                return;
            }

            DeleteMessageReply replyMessage = new DeleteMessageReply(Message.getMessageBody(reply));

            System.out.println("Delete operation has " + replyMessage.getAckMessage());
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    private void sendTCPFile(Socket socket, String filepath) throws IOException {
        File file = new File(filepath);
        OutputStream output = socket.getOutputStream();
        output.write(("get" + "\n").getBytes());
        FileInputStream in = new FileInputStream(file);
        int n;
        while ((n = in.read()) != -1)  {
            output.write(n);
        }
        output.write(("END\n").getBytes());
    }

    private String readTCPMessage(Socket socket) throws IOException {
        DataInputStream input = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String res = reader.readLine();

        if (!reader.readLine().equals("END")) {
            System.err.println("End of the response message failed");
            return null;
        }
        return res;
    }

    private File readTCPFile(Socket socket, String filepath) throws IOException {
        DataInputStream input = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String res = reader.readLine();
        File file = new File(filepath);

        if (res.equals("failed")) {
            return null;
        } else {
            try (FileOutputStream out = new FileOutputStream(file)) {
                String line;
                while ((line = reader.readLine()) != null && !line.equals("END")) {
                    out.write(line.getBytes());
                }
                if (line == null) {
                    System.err.println("End of the response message failed");
                    return null;
                }
            }
        }
        return file;
    }
}
