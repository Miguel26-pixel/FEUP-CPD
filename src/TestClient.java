import client.ClientServices;
import client.Services;

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

        ClientServices clientServices = new ClientServices(args);

        try {
            switch (args[1]) {
                case "join":
                    if (args.length > 2) {
                        System.err.println("Invalid number of arguments for join operation");
                        System.out.println("usage: java TestClient <node_ap> join");
                        System.exit(1);
                    }
                    clientServices.sendRMIJoin();
                    break;
                case "leave":
                    if (args.length > 2) {
                        System.err.println("Invalid number of arguments for leave operation");
                        System.out.println("usage: java TestClient <node_ap> leave");
                        System.exit(1);
                    }
                    clientServices.sendRMILeave();
                    break;
                case "put":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for put operation");
                        System.out.println("usage: java TestClient <node_ap> put <filepath>");
                        System.exit(1);
                    }
                    clientServices.handleTCPPut();
                    break;
                case "get":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for get operation");
                        System.out.println("usage: java TestClient <node_ap> get <encoded_key>");
                        System.exit(1);
                    }
                    clientServices.handleTCPGet();
                    break;
                case "delete":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for delete operation");
                        System.out.println("usage: java TestClient <node_ap> delete <encoded_key>");
                        System.exit(1);
                    }
                    clientServices.handleTCPDelete();
                    break;

                // for testing
                case "Hello":
                    Registry registry = LocateRegistry.getRegistry(args[0].split(":")[0]);
                    Services stub = (Services) registry.lookup(args[0].split(":")[1]);
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
}