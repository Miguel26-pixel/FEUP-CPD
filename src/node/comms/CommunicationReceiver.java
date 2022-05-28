package node.comms;

import node.membership.MembershipService;
import node.store.KeyValueStore;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommunicationReceiver extends Thread {
    protected final static int TIMEOUT = 3000;

    protected final AtomicBoolean running;
    protected final AtomicBoolean stop;

    public CommunicationReceiver() {
        this.running = new AtomicBoolean(true);
        this.stop = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        while (this.running.get()) {
            try {
                if (this.stop.get()) {
                    Thread.sleep(1);
                } else {
                    this.read();
                }
            } catch (Exception ignore) {}
        }
    }

    protected abstract void read();
}
