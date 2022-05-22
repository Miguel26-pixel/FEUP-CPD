import client.Services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClient {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Invalid number of arguments");
            System.out.println("usage: java TestClient <node_ap> <operation> [<opnd>]");
            System.exit(1);
        }

        if (args[0].split(":").length != 2) {
            System.err.println("Invalid node_ap argument");
            System.out.println("usage: java TestClient <IP>:<remote_name> <operation> [<opnd>]");
            System.exit(1);
        }

        String[] words = args[0].split(":");
        String nodeIP = words[0];
        String remoteObj = words[1];
        String operation = args[1];

        try {
            switch (operation) {
                case "join":
                    if (args.length > 2) {
                        System.err.println("Invalid number of arguments for join operation");
                        System.out.println("usage: java TestClient <node_ap> join");
                        System.exit(1);
                    }
                    sendRMIJoin();
                    break;
                case "leave":
                    if (args.length > 2) {
                        System.err.println("Invalid number of arguments for leave operation");
                        System.out.println("usage: java TestClient <node_ap> leave");
                        System.exit(1);
                    }
                    sendRMILeave();
                    break;
                case "put":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for put operation");
                        System.out.println("usage: java TestClient <node_ap> put <filepath>");
                        System.exit(1);
                    }
                    handleTCPPut(nodeIP, Integer.parseInt(remoteObj), args[2]);
                    break;
                case "get":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for get operation");
                        System.out.println("usage: java TestClient <node_ap> get <encoded_key>");
                        System.exit(1);
                    }
                    handleTCPGet(nodeIP, Integer.parseInt(remoteObj), args[2]);
                    break;
                case "delete":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for delete operation");
                        System.out.println("usage: java TestClient <node_ap> delete <encoded_key>");
                        System.exit(1);
                    }
                    handleTCPDelete(nodeIP, Integer.parseInt(remoteObj), args[2]);
                    break;

                // for testing
                case "Hello":
                    Registry registry = LocateRegistry.getRegistry(nodeIP);
                    Services stub = (Services) registry.lookup(remoteObj);
                    String response = stub.sayHello();
                    System.out.println("response: " + response);
                    break;
                default:
                    System.err.println("Invalid operation");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }

    private static void sendRMIJoin() {

    }

    private static void sendRMILeave() {

    }

    private static void handleTCPPut(String nodeIP, int port, String filepath) {
        try {
            Socket socket = new Socket(nodeIP, port);
            sendTCPMessage(socket, "put", filepath);
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    private static void handleTCPGet(String nodeIP, int port, String key) {
        try {
            Socket socket = new Socket(nodeIP, port);
            sendTCPMessage(socket, "get", key);
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    private static void handleTCPDelete(String nodeIP, int port, String key) {
        try {
            Socket socket = new Socket(nodeIP, port);
            sendTCPMessage(socket, "delete", key);
        } catch (IOException e) {
            System.out.println("Client exception" + e);
        }
    }

    private static void sendTCPMessage(Socket socket, String type, String arg) throws IOException {
        OutputStream output = socket.getOutputStream();
        output.write((type + ";" + arg).getBytes());
    }
}