package node.membership.view;

public class ViewEntry {
    private final int counter;
    private final int epoch;

    private final int port;

    public ViewEntry(int counter, int epoch, int port) {
        this.counter = counter;
        this.epoch = epoch;
        this.port = port;
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

    @Override
    public String toString() {
        return counter + ";" + epoch;
    }
}
