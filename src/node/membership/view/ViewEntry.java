package node.membership.view;

public class ViewEntry {
    private final int counter;
    private final int epoch;
    private final String port;
    private final String address;

    public ViewEntry(String port, String address, int counter, int epoch) {
        this.counter = counter;
        this.epoch = epoch;
        this.port = port;
        this.address = address;
    }

    public int getCounter() {
        return counter;
    }

    public int getEpoch() {
        return epoch;
    }

    public String getPort() { return port; }

    public String getAddress() { return address; }

    @Override
    public String toString() {
        return address + ";" + port + ";" + counter + ";" + epoch;
    }
}
