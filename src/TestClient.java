import client.Services;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class TestClient {
    public static void main(String[] args) {

        // to change
        if (args.length < 2) {
            System.err.println("usage: java TestClient <node_ap> // <operation> [<opnd>]");
            System.exit(1);
        }

        String[] words = args[0].split(":");
        String nodeIP = words[0];
        String remoteObj = words[1];

        switch (args[1]) {
            case "Hello":
                try {
                    Registry registry = LocateRegistry.getRegistry(nodeIP);
                    Services stub = (Services) registry.lookup(remoteObj);
                    String response = stub.sayHello();
                    System.out.println("response: " + response);
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }
                break;
            case "join":
            case "leave":
            case "put":
            case "get":
            case "delete":
            default:
                System.out.println("Invalid operation");
                break;
        }
    }
}
