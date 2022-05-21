package node.membership.view;

public class ViewEntry {
    private final int counter;
    private final int epoch;

    public ViewEntry(int counter, int epoch) {
        this.counter = counter;
        this.epoch = epoch;
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
