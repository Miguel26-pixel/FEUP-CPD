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
    private final String nodeIP;
    private final String operand;
    public ClientServices(String[] args) {
        String[] words = args[0].split(":");
        this.nodeIP = words[0];
        this.nodeArg = words[1];
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
        try (Socket socket = new Socket(nodeIP, port)){
            File file = new File(operand);
            if (!file.exists() || !file.isFile()) {
                System.err.println("File does not exists");
                return;
            }
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            PutMessage message = new PutMessage(file);
            UtilsTCP.sendTCPMessage(output, message);
            String reply = UtilsTCP.readTCPMessage(input);

            if (Message.getMessageType(reply) != MessageType.PUT_REPLY) {
                System.out.println("Operation put failed");
                System.err.println("Message reply incorrect type. Expected: " + MessageType.PUT_REPLY + ". Given: " + Message.getMessageType(reply));
                return;
            }

            PutMessageReply replyMessage = PutMessageReply.assembleMessage(Message.getMessageBody(reply));
            System.out.println("New key: " + replyMessage.getKey());
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    public void handleTCPGet() {
        int port = Integer.parseInt(nodeArg);
        try (Socket socket = new Socket(nodeIP, port)){
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();
            GetMessage message = new GetMessage(operand);
            UtilsTCP.sendTCPMessage(output, message);
            String reply = UtilsTCP.readTCPMessage(input);

            if (Message.getMessageType(reply) != MessageType.GET_REPLY) {
                System.out.println("Operation get failed");
                System.err.println("Message reply incorrect type. Expected: " + MessageType.GET_REPLY + ". Given: " + Message.getMessageType(reply));
                return;
            }

            if (GetMessageReply.isBodyEmpty(reply)) {
                System.err.println("File does not exist");
                return;
            }

            File testDir = new File("../clientFiles/");
            if (!testDir.exists() || !testDir.isDirectory()) {
                boolean res = testDir.mkdir();
                if(res) {
                    System.out.println("Client files folder created with success");
                } else {
                    System.err.println("Client files folder could not be created");
                    return;
                }
            }

            GetMessageReply replyMessage = GetMessageReply.assembleMessage(Message.getMessageBody(reply), "../clientFiles/file_" + operand);
            System.out.println("File obtained with success (saved as " + replyMessage.getFile().getName() + " in clientFiles folder)");
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    public void handleTCPDelete() {
        int port = Integer.parseInt(nodeArg);
        try (Socket socket = new Socket(nodeIP, port)){
            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            DeleteMessage message = new DeleteMessage(operand);
            UtilsTCP.sendTCPMessage(output, message);
            String reply = UtilsTCP.readTCPMessage(input);

            if (Message.getMessageType(reply) != MessageType.DELETE_REPLY) {
                System.out.println("Operation delete failed");
                System.err.println("Message reply incorrect type. Expected: " + MessageType.DELETE_REPLY + ". Given: " + Message.getMessageType(reply));
                return;
            }

            DeleteMessageReply replyMessage = DeleteMessageReply.assembleMessage(Message.getMessageBody(reply));
            System.out.println("Delete operation has " + replyMessage.getAckMessage());
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }
}
