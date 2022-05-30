package node.store;

import message.Message;
import message.messages.GetMessage;
import node.membership.threading.JoinTask;
import node.membership.threading.LeaveTask;
import node.membership.threading.MembershipTask;
import node.store.threading.DeleteTask;
import node.store.threading.GetTask;
import node.store.threading.PutTask;
import threading.ThreadPool;
import utils.UtilsFile;
import utils.UtilsHash;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class KeyValueStore {
    private final ArrayList<String> idStore;
    private final String folderPath;
    private final String folderName;
    private final ThreadPool workers;

    public KeyValueStore(String folderName, ThreadPool workers){
        this.idStore = new ArrayList<>();
        this.folderPath = "../dynamo/";
        this.folderName = folderName;
        this.workers = workers;
        checkPastFiles();
    }

    private void checkPastFiles() {
        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();

            if (files == null) { return; }

            for (File file : files) {
                String keyStr = file.getName().substring(("file_").length());
                idStore.add(keyStr);
            }
        }
    }


    public void processGet(String getMessageString, Socket socket) {
        workers.execute(new GetTask(getMessageString, socket, folderPath, folderName, idStore));
    }

    public void processPut(String putMessageString, Socket socket) {
        workers.execute(new PutTask(putMessageString, socket, folderPath, folderName, idStore));
    }

    public void processDelete(String deleteMessageString, Socket socket) {
        workers.execute(new DeleteTask(deleteMessageString, socket, folderPath, folderName, idStore));
    }
}
