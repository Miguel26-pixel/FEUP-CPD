package node.membership.threading;

import message.messages.JoinMessage;
import message.messages.MembershipMessage;
import message.messages.PutMessage;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import node.store.KeyValueStore;
import threading.ThreadPool;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class JoinTask extends Thread {
    private final View view;
    private final String joinMessageString;
    private final KeyValueStore keyValueStore;
    private final ThreadPool workers;

    public JoinTask(View view, String joinMessage, KeyValueStore keyValueStore, ThreadPool workers) {
        this.view = view;
        this.joinMessageString = joinMessage;
        this.keyValueStore = keyValueStore;
        this.workers = workers;
    }

    @Override
    public void run() {
        JoinMessage joinMessage = new JoinMessage(joinMessageString);

        if (!this.isCounterCorrect(joinMessage.getCounter())) {
            return;
        }

        long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        this.view.addEntry(joinMessage.getOriginId(), new ViewEntry(joinMessage.getPort(), joinMessage.getOriginId(), joinMessage.getCounter(), secondsSinceEpoch), true);

        MembershipMessage reply = new MembershipMessage(this.view);

        try (Socket socket = new Socket(joinMessage.getOriginId(), joinMessage.getPort())){
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            UtilsTCP.sendTCPMessage(output, reply);
        } catch (IOException ignored) {
        }

        Map<String,String> files_to_change = keyValueStore.checkFilesView(view);

        for (Map.Entry<String, String> entry : files_to_change.entrySet()) {
            String fileKey = entry.getValue().substring(entry.getValue().lastIndexOf("file_") + ("file_").length());
            workers.execute(new SendPutTask(view.getUpEntries().get(entry.getKey()).getAddress(),
                    view.getUpEntries().get(entry.getKey()).getPort(), new PutMessage(new File(entry.getValue())),
                    keyValueStore, fileKey));
        }
    }

    private boolean isCounterCorrect(int counter) {
        return counter % 2 == 0;
    }
}
