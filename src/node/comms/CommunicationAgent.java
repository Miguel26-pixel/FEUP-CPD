package node.comms;

import message.Message;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommunicationAgent extends Thread {
    protected final static int TIMEOUT = 3000;

    protected final AtomicBoolean isAlive;
    protected final AtomicBoolean stop;

    public CommunicationAgent() {
        this.isAlive = new AtomicBoolean(true);
        this.stop = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        while (this.isAlive.get()) {
            try {
                if (this.stop.get()) {
                    Thread.sleep(1);
                } else {
                    this.read();
                }
            } catch (Exception ignore) {}
        }
    }

    public void stopExecution() {
        this.stop.set(true);
    }

    public void resumeExecution() {
        this.stop.set(false);
    }

    public void killReceiver() {
        this.isAlive.set(false);
    }

    protected abstract void read();
}
