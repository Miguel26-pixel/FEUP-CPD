import node.Node;
import utils.UtilsIP;

public class Store {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("usage: java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>");
            System.exit(1);
        }

        if (UtilsIP.isIPValid(args[0])) {
            System.err.println("Invalid multicast IP address");
            System.exit(1);
        }

        if (UtilsIP.isPortValid(args[1])) {
            System.err.println("Invalid multicast IP port");
            System.exit(1);
        }

        if (UtilsIP.isIPValid(args[2])) {
            System.err.println("Invalid node IP address");
            System.exit(1);
        }

        if (UtilsIP.isPortValid(args[3])) {
            System.err.println("Invalid multicast IP port");
            System.exit(1);
        }

        try{
            Integer.parseInt(args[1]);
            Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port");
        }

        try {
            Node node = new Node(args[0], args[1], args[2], args[3]);

            if (!RMIServer.register(node,args[2])) {
                System.err.println("RMI register failed");
                System.exit(1);
            }

            node.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
