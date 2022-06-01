package node.store.threading;

import message.Message;
import node.store.KeyValueStore;

public class ForcePutTask extends Thread {
    private final String forcePutMessageString;
    private final KeyValueStore keyValueStore;

    public ForcePutTask(String forcePutMessageString, KeyValueStore keyValueStore) {
        this.forcePutMessageString = forcePutMessageString;
        this.keyValueStore = keyValueStore;
    }

    @Override
    public void run() {
        String newKey = keyValueStore.putNewPair(Message.getMessageBody(forcePutMessageString));
        if (newKey == null) {
            System.err.println("Put operation failed");
        } else {
            System.out.println("New key: " + newKey);
        }
    }
}
