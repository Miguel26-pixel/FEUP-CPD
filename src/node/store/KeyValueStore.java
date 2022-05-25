package node.store;

import utils.UtilsFile;
import utils.UtilsHash;

import java.io.*;
import java.util.*;

public class KeyValueStore {
    private final ArrayList<String> keys;
    private final String folderPath;
    private final String folderName;

    public KeyValueStore(String folderName){
        this.keys = new ArrayList<>();
        this.folderPath = "../dynamo/";
        this.folderName = folderName;
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

        keys.add(valueKey);

        return valueKey;
    }

    public File getValue(String key){
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

    public String deleteValue(String key){
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
