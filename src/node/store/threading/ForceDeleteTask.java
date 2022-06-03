package node.store.threading;

import message.Message;
import node.store.KeyValueStore;

public class ForceDeleteTask extends Thread {
    private final String forceDeleteMessageString;
    private final KeyValueStore keyValueStore;

    public ForceDeleteTask(String forceDeleteMessageString, KeyValueStore keyValueStore) {
        this.forceDeleteMessageString = forceDeleteMessageString;
        this.keyValueStore = keyValueStore;
    }

    @Override
    public void run() {
        String state = keyValueStore.deleteValue(Message.getMessageBody(forceDeleteMessageString));
        System.out.println("Delete operation has " + state);
    }
}
