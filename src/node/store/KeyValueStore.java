package node.store;

import message.Message;
import message.messages.*;
import node.membership.view.View;
import node.membership.view.ViewEntry;
import utils.UtilsHash;
import utils.UtilsTCP;

import java.io.*;
import java.util.*;

public class KeyValueStore {
    private final ArrayList<String> keys;
    private final String folderPath;
    private final String folderName;
    private final String myHash;

    public String handlePut(String message, View view) {
        String file = Message.getMessageBody(message);
        String fileKey = UtilsHash.hashSHA256(file);
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            System.err.println("Successor node not found");
            return new String((new PutMessageReply("Successor node not found")).assemble());
        } else if (nodeHash.equals(myHash)) {
            String key = putNewPair(file);
            System.out.println("New key: " + key);
            return new String((new PutMessageReply(key)).assemble());
        }
        ViewEntry entry = view.getEntries().get(nodeHash);
        return UtilsTCP.redirectMessage(message,entry.getAddress(), entry.getPort());
    }

    public String handleGet(String message, View view) {
        GetMessage getMessage = GetMessage.assembleMessage(Message.getMessageBody(message));
        String fileKey = getMessage.getKey();
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            System.err.println("Successor node not found");
            return new String((new GetMessageReply(null)).assemble());
        } else if (nodeHash.equals(myHash)) {
            File file = getValue(fileKey);
            System.out.println((file == null) ? "File does not exists" : "File obtained with success");
            return new String((new GetMessageReply(file)).assemble());
        }
        ViewEntry entry = view.getEntries().get(nodeHash);
        return UtilsTCP.redirectMessage(message,entry.getAddress(), entry.getPort());
    }

    public String handleDelete(String message, View view) {
        DeleteMessage deleteMessage = DeleteMessage.assembleMessage(Message.getMessageBody(message));
        String fileKey = deleteMessage.getKey();
        String nodeHash = getClosestNodeKey(fileKey, view);
        if (nodeHash == null) {
            System.err.println("Successor node not found");
            return new String((new DeleteMessageReply("failed")).assemble());
        } else if (nodeHash.equals(myHash)) {
            String state = deleteValue(fileKey);
            System.out.println("Delete operation has " + state);
            return new String((new DeleteMessageReply(state)).assemble());
        }
        ViewEntry entry = view.getEntries().get(nodeHash);
        return UtilsTCP.redirectMessage(message,entry.getAddress(), entry.getPort());
    }

    public KeyValueStore(String folderName, String myHash){
        this.keys = new ArrayList<>();
        this.folderPath = "../dynamo/";
        this.folderName = folderName;
        this.myHash = myHash;
        checkPastFiles();
    }

    private void checkPastFiles() {
        File nodeDir = new File(folderPath + folderName);
        if (nodeDir.exists() && nodeDir.isDirectory() && nodeDir.listFiles() != null) {
            File[] files = nodeDir.listFiles();

            if (files == null) { return; }

            for (File file : files) {
                String keyStr = file.getName().substring(("file_").length());
                keys.add(keyStr);
            }
        }
    }

    private String getClosestNodeKey(String hash, View view) {
        if (view.getEntries().isEmpty()) { return null; }
        for (String key: view.getEntries().keySet()) {
            if (key.compareTo(hash) > 0) { return key; }
        }
        return view.getEntries().keySet().iterator().next();
    }

    private String putNewPair(String file) {
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

        keys.add(valueKey);

        return valueKey;
    }

    private File getValue(String key){
        for (String existingKey : keys) {
            if (existingKey.equals(key)) {
                File file = new File(folderPath + folderName + "/file_" + key);
                if (file.exists() && file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    private String deleteValue(String key){
        String path = "";
        int index = -1;
        for (int i = 0; i < keys.size(); i++){
            if (keys.get(i).equals(key)) {
                path = folderPath + folderName + "/file_" + key;
                index = i;
                break;
            }
        }

        if (index == -1) { return "failed"; }

        File file = new File(path);
        if (!file.exists() || !file.delete()) { return "failed"; }

        keys.remove(index);
        return "succeeded";
    }
}
