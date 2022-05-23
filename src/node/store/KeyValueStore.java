package node.store;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class KeyValueStore {
    private ArrayList<Integer> idStore;
    private String folderPath;
    private String folderName;

    public KeyValueStore(String folderName){
        this.idStore = new ArrayList<Integer>();
        this.folderPath = "../dynamo/";
        this.folderName = folderName;
    }


    public String putNewPair(String pathname) {
        Integer valueKey = idStore.size();
        File file = new File(pathname);

        if (!file.exists() || !file.isFile()) {
            System.err.println("Invalid pathname");
            return "Invalid pathname" + pathname;
        }

        File dynamoDir = new File(folderPath);
        if (!dynamoDir.exists() || !dynamoDir.isDirectory()) {
            boolean res = dynamoDir.mkdir();
            if(res) {
                System.out.println("Dynamo main folder created with success");
            } else {
                System.err.println("Dynamo main folder could not be created");
                return "Dynamo main folder could not be created";
            }
        }

        File nodeDir = new File(folderPath + folderName);
        if (!nodeDir.exists() || !nodeDir.isDirectory()) {
            boolean res = nodeDir.mkdir();
            if(res) {
                System.out.println("Node folder created with success");
            } else {
                System.err.println("Node folder could not be created");
                return "Node folder could not be created";
            }
        }

        File newFile = new File(folderPath + folderName + "/file_" + valueKey);

        if (!this.copyFile(file, newFile)) {
            return "File could not be created";
        }

        idStore.add(valueKey);

        return String.valueOf(valueKey);
    }

    private boolean copyFile(File a, File b) {
        try (FileInputStream in = new FileInputStream(a); FileOutputStream out = new FileOutputStream(b)) {
            int n;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
        } catch (IOException e) {
            System.err.println("File could not be copied");
            return false;
        }
        return true;
    }

    public File getValue(String key){
        int parsed_key = Integer.parseInt(key);
        for (Integer integer : idStore) {
            if (integer.equals(parsed_key)) {
                File file = new File(folderPath + folderName + "/file_" + parsed_key);
                if (file.exists() && file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    public boolean deleteValue(String key){
        String path = "";
        int index = -1;
        int parsed_key = Integer.parseInt(key);
        for (int i = 0; i < idStore.size(); i++){
            if (idStore.get(i) == parsed_key) {
                 path = folderPath + folderName + "/file_" + parsed_key;
                 index = i;
                 break;
            }
        }

        if (index == -1) { return false; }

        File file = new File(path);
        if (!file.exists() || !file.delete()) { return false; }

        idStore.remove(index);
        return true;
    }
}
