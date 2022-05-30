package node.store.threading;

public class PutTask extends Thread {
    private String putMessageString;

    public PutTask(String putMessageString) {
        this.putMessageString = putMessageString;
    }

    @Override
    public void run() {

    }
}
