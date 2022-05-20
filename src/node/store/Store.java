package node.store;

import java.util.HashMap;
import java.io.File;  
import java.io.IOException;  

public class Store {
    private HashMap<Integer,String> idStore;

    public Store(){
        this.idStore = new HashMap<Integer,String>();
    }


    private void putNode(String pathname){
        Integer actual_id = idStore.size();
        idStore.put(actual_id,pathname);

        File file = new File(pathname); 
        boolean result;  
        try   
        {  
        result = file.createNewFile();  
        if(result)       
        {  
        System.out.println("file created "+file.getCanonicalPath() + " with key " + actual_id);   
        }  
        else  
        {  
        System.out.println("File already exist at location: "+file.getCanonicalPath());  
        }  
        }   
        catch (IOException e)   
        {  
        e.printStackTrace();    
        }    

    }

    private String getNode(Integer key){
        return idStore.get(key);
    }

    private void deleteNode(Integer key){
        String pathname = idStore.get(key);

        File myObj = new File(pathname); 
        if (myObj.delete()) { 
            System.out.println("Deleted the file: " + myObj.getName());
        } else {
            System.out.println("Failed to delete the file.");
        } 

        idStore.remove(key);

    }


}
