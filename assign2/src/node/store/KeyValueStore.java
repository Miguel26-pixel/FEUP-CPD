package node.store;

import message.Message;
import message.messages.*;
import node.membership.threading.JoinTask;
import node.membership.threading.LeaveTask;
import node.membership.threading.MembershipTask;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import node.store.threading.*;
import threading.ThreadPool;
import utils.UtilsFile;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class KeyValueStore {
    private final ArrayList<String> idStore;
    private final String folderPath;
    private final String folderName;
    private final ThreadPool workers;
    private final View view;
    private final String myHash;

    public KeyValueStore(String folderName, String myHash, ThreadPool workers, View view){
        this.idStore = new ArrayList<>();
        this.folderPath = "../dynamo/";
        this.folderName = folderName;
        this.workers = workers;
        this.view = view;
        this.myHash = myHash;
    }

    public void checkPastFiles() {
        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();

            if (files == null) { return; }

            for (File file : files) {
                String keyStr = file.getName().substring(("file_").length());
                idStore.add(keyStr);
                System.out.println("key added: " + keyStr);
            }
        }
    }

    private String getClosestNodeKey(String hash, View view) {
        if (view.getUpEntries().isEmpty()) { return null; }
        for (String key: view.getUpEntries().keySet()) {
            if (key.compareTo(hash) > 0) { return key; }
        }
        return view.getUpEntries().keySet().iterator().next();
    }

    public Map<String,String> checkFilesView(View view) {
        Map<String,String> files_to_change = new HashMap<>();

        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();
            if (files == null) { return files_to_change; }

            for (File file : files) {
                String hash = file.getName().substring(("file_").length());
                String nodeKey = getClosestNodeKey(hash,view);
                if (nodeKey != null && !nodeKey.equals(myHash)){
                    files_to_change.put(nodeKey,folderPath + folderName + "/" + file.getName());
                }
            }
        }
        return files_to_change;
    }


    public void processGet(String getMessageString, Socket socket) {
        String file = Message.getMessageBody(getMessageString);
        String fileKey = UtilsHash.hashSHA256(file);
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            workers.execute(new GetFailedTask(new GetMessageReply(null), socket));
        } else if (nodeHash.equals(myHash)) {
            workers.execute(new GetTask(getMessageString, socket, folderPath, folderName, idStore));
        } else {
            ViewEntry entry = view.getUpEntries().get(nodeHash);
            workers.execute(new RedirectTask(socket, getMessageString, entry.getAddress(), entry.getPort()));
        }
    }

    public void processPut(String putMessageString, Socket socket) {
        String file = Message.getMessageBody(putMessageString);
        String fileKey = UtilsHash.hashSHA256(file);
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            workers.execute(new PutFailedTask(new PutMessageReply("Successor node not found"), socket));
        } else if (nodeHash.equals(myHash)) {
            workers.execute(new PutTask(putMessageString, socket, this));
        } else {
            ViewEntry entry = view.getUpEntries().get(nodeHash);
            workers.execute(new RedirectTask(socket, putMessageString, entry.getAddress(), entry.getPort()));
        }
    }

    public void processForcePut(String forcePutMessage) {
        workers.execute(new ForcePutTask(forcePutMessage, this));
    }

    public void processDelete(String deleteMessageString, Socket socket) {
        String file = Message.getMessageBody(deleteMessageString);
        String fileKey = UtilsHash.hashSHA256(file);
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            workers.execute(new DeleteTaskFailed(new DeleteMessageReply("Successor node not found"), socket));
        } else if (nodeHash.equals(myHash)) {
            workers.execute(new DeleteTask(deleteMessageString, socket, folderPath, folderName, idStore));
        } else {
            ViewEntry entry = view.getUpEntries().get(nodeHash);
            workers.execute(new RedirectTask(socket, deleteMessageString, entry.getAddress(), entry.getPort()));
        }
    }

    public void sendFilesToNextNode() {
        while (idStore.size() > 0) {
            String key = idStore.get(0);
            File file = new File(folderPath + folderName + "/file_" + key);
            String nodeKey = getClosestNodeKey(myHash,view);

            if (nodeKey == null) break;
            if (nodeKey.equals(myHash)) {
                System.out.println("Removi");
                idStore.remove(0);
                continue;
            }

            new SendForcePutTask(view.getUpEntries().get(nodeKey).getAddress(),
                    view.getUpEntries().get(nodeKey).getPort(),
                    new ForcePutMessage(file),this, key).sendForcePut();
        }
    }

    public String putNewPair(String file) {
        String valueKey = UtilsHash.hashSHA256(file);

        File dynamoDir = new File(folderPath);
        if (!dynamoDir.exists() || !dynamoDir.isDirectory()) {
            boolean res = dynamoDir.mkdir();
            if(res) {
                System.out.println("Dynamo main folder created with success");
            } else {
                System.err.println("Dynamo main folder could not be created");
                return null;
            }
        }

        File nodeDir = new File(folderPath + folderName);
        if (!nodeDir.exists() || !nodeDir.isDirectory()) {
            boolean res = nodeDir.mkdir();
            if(res) {
                System.out.println("Node folder created with success");
            } else {
                System.err.println("Node folder could not be created");
                return null;
            }
        }

        File newFile = new File(folderPath + folderName + "/file_" + valueKey);

        try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!idStore.contains(valueKey)) {
            idStore.add(valueKey);
        }

        return valueKey;
    }

    public String deleteValue(String key){
        int index = -1;
        for (int i = 0; i < idStore.size(); i++){
            if (idStore.get(i).equals(key)) {
                index = i;
                break;
            }
        }

        File file = new File(folderPath + folderName + "/file_" + key);
        if (!file.exists() || !file.delete()) { return "failed"; }

        idStore.remove(index);
        return "succeeded";
    }
}
