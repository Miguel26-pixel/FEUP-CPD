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


    private String putNode(String pathname) {
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

    private String getNode(Integer key){
        for (int i = 0; i < idStore.size(); i++){
            if (Objects.equals(idStore.get(i), key)) {
                return folderPath + "/folder" + key + "/file" + key;
            }
        }
        return "don't exist";
    }

    private void deleteNode(Integer key){
        String path = "";
        Integer index = 0;
        for (int i = 0; i < idStore.size(); i++){
            if (Objects.equals(idStore.get(i), key)) {
                 path = folderPath + "/folder" + key + "/file" + key;
                 index = i;
            }
        }

        if (!path.equals("")) {
            File myObj = new File(path);

            if (myObj.delete()) { 
                System.out.println("Deleted the file: " + myObj.getName());
            } else {
                System.out.println("Failed to delete the file.");
            }

            idStore.remove(index);
        }
    }
}
