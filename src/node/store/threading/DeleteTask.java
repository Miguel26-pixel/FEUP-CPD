package node.store.threading;

public class DeleteTask extends Thread {
    private String deleteMessageString;

    public DeleteTask(String deleteMessageString) {
        this.deleteMessageString = deleteMessageString;
    }

    @Override
    public void run() {

    }
}
