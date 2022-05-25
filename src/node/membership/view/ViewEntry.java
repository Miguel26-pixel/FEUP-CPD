package node.membership.view;

public class ViewEntry {
    private final int counter;
    private final int epoch;

    private final int port;
    private final String address;

    public ViewEntry(int counter, int epoch, int port, String address) {
        this.counter = counter;
        this.epoch = epoch;
        this.port = port;
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public int getCounter() {
        return counter;
    }

    public int getEpoch() {
        return epoch;
    }

    public String getAddress() { return address; }

    @Override
    public String toString() {
        return counter + ";" + epoch;
    }
}
