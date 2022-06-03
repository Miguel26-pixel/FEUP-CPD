package node.membership.view;

public class ViewEntry {
    public final static int INVALID_INT = -1;

    private final int counter;
    private final long epoch;
    private final int port;
    private final String address;

    public ViewEntry(int port, String address, int counter, long epoch) {
        this.counter = counter;
        this.epoch = epoch;
        this.port = port;
        this.address = address;
    }

    public int getCounter() {
        return counter;
    }

    public long getEpoch() {
        return epoch;
    }

    public int getPort() { return port; }

    public String getAddress() { return address; }

    @Override
    public String toString() {
        return address + ";" + port + ";" + counter + ";" + epoch;
    }
}
