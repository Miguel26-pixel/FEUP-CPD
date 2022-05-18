import node.Node;

public class Store {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("usage: java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>");
            System.exit(1);
        }

        Node node = new Node(args[0], args[1], args[2], args[3]);
        node.start();

        System.exit(0);
    }
}
