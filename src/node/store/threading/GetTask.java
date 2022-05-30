package node.store.threading;

public class GetTask extends Thread {
    private String getMessageString;

    public GetTask(String getMessageString) {
        this.getMessageString = getMessageString;
    }

    @Override
    public void run() {

    }
}
