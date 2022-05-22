package node.store;

import java.util.HashMap;
import java.io.File;  
import java.util.ArrayList;
import java.io.IOException;  

public class Store {
    private ArrayList<Integer> idStore;
    private String dynamo_path;

    public Store(){
        this.idStore = new ArrayList<Integer>();
        this.dynamo_path = "/home/poker/Documents/feup-cpd-dynamo/src/dynamo";
    }


    private void putNode(String pathname){
        Integer actual_id = idStore.size();
        idStore.add(actual_id);

        File file1 = new File(pathname + "/folder" + actual_id); 

        boolean folder_result;  
        try   
        {  
        folder_result = file1.mkdir();  
        if(folder_result)       
        {  
        System.out.println("folder created "+file1.getCanonicalPath() + " with key " + actual_id);   
        }  
        else  
        {  
        System.out.println("Folder already exist at location: "+file1.getCanonicalPath());  
        }  
        }   
        catch (IOException e)   
        {  
        e.printStackTrace();    
        } 
        
        
        File file2 = new File(pathname + "/folder" + actual_id + "/file" + actual_id); 

        boolean file_result;  
        try   
        {  
        file_result = file2.createNewFile();  
        if(file_result)       
        {  
        System.out.println("file created "+file2.getCanonicalPath() + " with key " + actual_id);   
        }  
        else  
        {  
        System.out.println("file already exist at location: "+file2.getCanonicalPath());  
        }  
        }   
        catch (IOException e)   
        {  
        e.printStackTrace();    
        } 
        
        

    }

    private String getNode(Integer key){

        for (Integer i = 0; i < idStore.size(); i++){
            if (idStore.get(i) == key) {
                return dynamo_path + "/folder" + key + "/file" + key;
            }
        }
        return "don't exist";
    }

    private void deleteNode(Integer key){
        String path = "";
        Integer index = 0;
        for (Integer i = 0; i < idStore.size(); i++){
            if (idStore.get(i) == key) {
                 path = dynamo_path + "/folder" + key + "/file" + key;
                 index = i;
            }
        }

        if (path != "") {
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
