package threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadPool {
    private BlockingQueue<Thread> tasks;
    private List<PoolThread> threads;
    private boolean closed;

    public ThreadPool(int threadsNum, int maxTasksNum) {
        this.tasks = new ArrayBlockingQueue(maxTasksNum);
        this.threads = new ArrayList<>();
        this.closed = false;

        for (int i = 0; i < threadsNum; i++) {
            threads.add(new PoolThread(this.tasks));
        }

        for (PoolThread thread: threads) {
            thread.start();
        }
    }

    public synchronized void execute(Thread task) {
        if (!closed) {
            tasks.add(task);
        }
    }

    public synchronized void stop() {
        if (!closed) {
            for (PoolThread thread: threads) {
                thread.stopThread();
            }
        }
    }

    public void waitForTasks() {
        while (tasks.size() > 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
