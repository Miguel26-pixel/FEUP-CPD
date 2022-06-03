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
        this.folderPath = "dynamo/";
        this.folderName = folderName;
        this.workers = workers;
        this.view = view;
        this.myHash = myHash;
    }

    public String getDirPath() {
        return folderPath + folderName + "/";
    }

    public void checkPastFiles() {
        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();

            if (files == null) { return; }

            for (File file : files) {
                if (!file.getName().contains("file_")) { continue; }
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

    private boolean isActive(String nodeKey) {
        try (Socket socket = new Socket(view.getUpEntries().get(nodeKey).getAddress(), view.getUpEntries().get(nodeKey).getPort())){
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    private String getFirstActiveNode(String hash) {
        String currentNode = getClosestNodeKey(hash, view);
        if (currentNode == null) { return null; }
        while (!currentNode.equals(myHash)) {
            if (isActive(currentNode)) { return currentNode; }
            currentNode = getClosestNodeKey(currentNode, view);
            if (currentNode == null) { return null; }
        }
        return myHash;
    }

    public List<Socket> getNextTwoActiveNodes() {
        List<Socket> sockets = new ArrayList<>();
        int counter = 0;
        String currentHash = getClosestNodeKey(myHash, view);
        if (currentHash == null) { return sockets; }
        while(counter < 2 && !currentHash.equals(myHash)) {
            try {
                Socket socket = new Socket(view.getUpEntries().get(currentHash).getAddress(), view.getUpEntries().get(currentHash).getPort());
                sockets.add(socket);
                counter++;
                currentHash = getClosestNodeKey(currentHash, view);
                if (currentHash == null) { return sockets; }
            } catch (IOException ignored) {
                currentHash = getClosestNodeKey(currentHash, view);
                if (currentHash == null) { return sockets; }
            }
        }
        return sockets;
    }

    public List<String> getActiveNodesToReplicate(String fileKey) {
        List<String> nodeKeys = new ArrayList<>();
        int counter = 0;
        String currentHash = getClosestNodeKey(fileKey, view);
        String firstHash = currentHash;
        do {
            if (currentHash == null) { break; }
            if (!currentHash.equals(myHash) && isActive(currentHash)) {
                nodeKeys.add(currentHash);
                counter++;
            }
            currentHash = getClosestNodeKey(currentHash, view);
        } while (counter < 3 && !firstHash.equals(currentHash));
        return nodeKeys;
    }

    public Map<String,String> checkFilesView(View view) {
        Map<String,String> files_to_change = new HashMap<>();

        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();
            if (files == null) { return files_to_change; }

            for (File file : files) {
                if (!file.getName().contains("file_")) { continue; }
                String hash = file.getName().substring(("file_").length());
                String nodeKey = getFirstActiveNode(hash);
                if (nodeKey != null && !nodeKey.equals(myHash)){
                    files_to_change.put(nodeKey, getDirPath() + file.getName());
                }
            }
        }
        return files_to_change;
    }

    public List<String> checkFilesReplication(String nodeID) {
        List<String> filesToReplicate = new ArrayList<>();
        String nodeKey = UtilsHash.hashSHA256(nodeID);
        String currentHash = myHash;
        for (String key: idStore) {
            for (int i = 0; i < 2; i++) {
                currentHash = getClosestNodeKey(currentHash,view);
                if (currentHash == null || currentHash.equals(myHash)) { break; }
                if (currentHash.equals(nodeKey) && myHash.equals(getClosestNodeKey(key,view))) {
                    filesToReplicate.add(getDirPath() + "file_" + key);
                }
            }
        }
        return filesToReplicate;
    }


    public void processGet(String getMessageString, Socket socket) {
        String file = Message.getMessageBody(getMessageString);
        String fileKey = UtilsHash.hashSHA256(file);
        String nodeHash = getFirstActiveNode(fileKey);
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
        String nodeHash = getFirstActiveNode(fileKey);
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
        String nodeHash = getFirstActiveNode(fileKey);
        if (nodeHash == null) {
            workers.execute(new DeleteTaskFailed(new DeleteMessageReply("Successor node not found"), socket));
        } else if (nodeHash.equals(myHash)) {
            workers.execute(new DeleteTask(deleteMessageString, socket, this));
        } else {
            ViewEntry entry = view.getUpEntries().get(nodeHash);
            workers.execute(new RedirectTask(socket, deleteMessageString, entry.getAddress(), entry.getPort()));
        }
    }

    public void processForceDelete(String forceDeleteMessage) {
        workers.execute(new ForceDeleteTask(forceDeleteMessage, this));
    }

    public void sendFilesToNextNodes() {
        while (idStore.size() > 0) {
            String key = idStore.get(0);
            File file = new File(getDirPath() + "file_" + key);
            List<String> nodeKeys = getActiveNodesToReplicate(key);
            for (String nodeKey: nodeKeys) {
                new SendForcePutTask(view.getUpEntries().get(nodeKey).getAddress(),
                        view.getUpEntries().get(nodeKey).getPort(),
                        new ForcePutMessage(file)).sendForcePut();
            }
            String state = deleteValue(key);
            System.out.println("File deleted operations has " + state);
        }
    }

    public String putNewPair(String file) {
        String valueKey = UtilsHash.hashSHA256(file);

        File newFile = new File(getDirPath() + "file_" + valueKey);

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
        if (index == -1) { return "failed"; }

        File file = new File(getDirPath() + "file_" + key);
        if (!file.exists() || !file.delete()) { return "failed"; }

        idStore.remove(index);
        return "succeeded";
    }
}
