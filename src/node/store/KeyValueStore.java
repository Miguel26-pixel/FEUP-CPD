package node.store;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class KeyValueStore {
    private ArrayList<Integer> idStore;
    private String folderPath;

    public KeyValueStore(){
        this.idStore = new ArrayList<Integer>();
        this.folderPath = "../dynamo/";
    }


    public String putNewPair(String pathname) {
        byte[] key;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            key = digest.digest(pathname.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm does not exist: " + e);
            e.printStackTrace();
            return "";
        }

        Integer actual_id = idStore.size();
        idStore.add(actual_id);

        File file1 = new File(pathname + "/folder" + actual_id); 

        boolean folder_result;  
        try {
            folder_result = file1.mkdir();
            if(folder_result) {
                System.out.println("folder created "+file1.getCanonicalPath() + " with key " + actual_id);
            } else {
                System.out.println("Folder already exist at location: " + file1.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        File file2 = new File(pathname + "/folder" + actual_id + "/file" + actual_id);

        boolean file_result;  
        try {
            file_result = file2.createNewFile();
            if(file_result) {
                System.out.println("file created "+ file2.getCanonicalPath() + " with key " + actual_id);
            } else {
                System.out.println("file already exist at location: " + file2.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getValue(String key){
        int parsed_key = Integer.parseInt(key);
        for (Integer integer : idStore) {
            if (Objects.equals(integer, parsed_key)) {
                return folderPath + "/folder" + parsed_key + "/file" + parsed_key;
            }
        }
        return "don't exist";
    }

    public boolean deleteValue(String key){
        String path = "";
        int index = -1;
        int parsed_key = Integer.parseInt(key);
        for (int i = 0; i < idStore.size(); i++){
            if (Objects.equals(idStore.get(i), parsed_key)) {
                 path = folderPath + "/folder" + parsed_key + "/file" + parsed_key;
                 index = i;
            }
        }

        if (index == -1 || path.equals("")) { return false; }

        File file = new File(path);
        if (!file.delete()) { return false; }

        idStore.remove(index);
        return true;
    }
}
