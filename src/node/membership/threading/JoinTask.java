package node.membership.threading;

import message.messages.*;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import node.store.KeyValueStore;
import node.store.threading.ForcePutTask;
import node.store.threading.SendForcePutTask;
import threading.ThreadPool;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class JoinTask extends Thread {
    private final View view;
    private final String joinMessageString;
    private final KeyValueStore keyValueStore;
    private final ThreadPool workers;
    private final String myID;

    public JoinTask(View view, String joinMessage, KeyValueStore keyValueStore, ThreadPool workers, String myID) {
        this.view = view;
        this.joinMessageString = joinMessage;
        this.keyValueStore = keyValueStore;
        this.workers = workers;
        this.myID = myID;
    }

    @Override
    public void run() {
        JoinMessage joinMessage = new JoinMessage(joinMessageString);
        int knownCounter = this.view.getCounter(UtilsHash.hashSHA256(joinMessage.getOriginId()));

        if (!this.isCounterCorrect(joinMessage.getCounter(), knownCounter)) {
            try {
                Socket socket = new Socket(joinMessage.getOriginId(), joinMessage.getPort());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                UtilsTCP.sendTCPMessage(output, new JoinReplyMessage(joinMessage.getOriginId(), knownCounter));
                System.out.println("[M]Informing " + joinMessage.getOriginId() + " that its counter should be " + knownCounter + " or above.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        long secondsSinceEpoch = System.currentTimeMillis() / 1000;
        this.view.addEntry(joinMessage.getOriginId(), new ViewEntry(joinMessage.getPort(), joinMessage.getOriginId(), joinMessage.getCounter(), secondsSinceEpoch), true);

        MembershipMessage reply = new MembershipMessage(this.view);

        try (Socket socket = new Socket(joinMessage.getOriginId(), joinMessage.getPort())){
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            UtilsTCP.sendTCPMessage(output, reply);
        } catch (IOException ignored) {}

        Map<String,String> files_to_change = keyValueStore.checkFilesView(view);

        for (Map.Entry<String, String> entry : files_to_change.entrySet()) {
            String fileKey = entry.getValue().substring(entry.getValue().lastIndexOf("file_") + ("file_").length());
            new SendPutTask(view.getUpEntries().get(entry.getKey()).getAddress(),
                    view.getUpEntries().get(entry.getKey()).getPort(), new PutMessage(new File(entry.getValue())),
                    keyValueStore, fileKey).run();
        }

        List<String> files_to_replicate = keyValueStore.checkFilesReplication(joinMessage.getOriginId());
        for (String file: files_to_replicate) {
            try (Socket socket = new Socket(joinMessage.getOriginId(), joinMessage.getPort())) {
                ForcePutMessage message = new ForcePutMessage(new File(file));
                UtilsTCP.sendTCPMessage(socket.getOutputStream(),message);
            } catch (IOException ignored) {}
        }

        System.out.println("[M]Received a join message from " + joinMessage.getOriginId() + " with counter " + joinMessage.getCounter());
    }

    private boolean isCounterCorrect(int receivedCounter, int knownCounter) {
        return (receivedCounter % 2) == 0 && receivedCounter >= knownCounter;
    }
}
