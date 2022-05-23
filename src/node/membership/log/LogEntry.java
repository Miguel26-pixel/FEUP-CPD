package node.membership.log;

public class LogEntry {
    private final int counter;
    private final int epoch;

    public LogEntry(int counter, int epoch) {
        this.counter = counter;
        this.epoch = epoch;
    }

    public int getCounter() {
        return counter;
    }

    public int getEpoch() {
        return epoch;
    }
}
