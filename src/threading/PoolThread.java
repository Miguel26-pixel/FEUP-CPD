package threading;

import java.util.concurrent.BlockingQueue;

public class PoolThread extends Thread {
    private final BlockingQueue<Thread> tasks;
    private Thread thread = null;
    private boolean stopped;

    public PoolThread(BlockingQueue<Thread> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while (!stopped) {
            try {
                Thread task = (Thread) tasks.take();
                task.run();
            } catch (Exception ignored) {
            }
        }
        super.run();
    }

    public synchronized void stopThread() {
        if (thread != null && !stopped) {
            this.thread.interrupt();
            this.stopped = true;
        }
    }

    public synchronized boolean isStopped() {
        return stopped;
    }
}
