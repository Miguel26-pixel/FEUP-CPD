package node.store.threading;

import message.Message;
import message.messages.DeleteMessageReply;
import message.messages.GetMessage;
import message.messages.PutMessageReply;
import utils.UtilsTCP;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class DeleteTask extends Thread {
    private final String deleteMessageString;
    private final String folderPath;
    private final String folderName;
    private final ArrayList<String> idStore;
    private final Socket socket;

    public DeleteTask(String deleteMessageString, Socket socket, String folderPath, String folderName, ArrayList<String> idStore) {
        this.deleteMessageString = deleteMessageString;
        this.socket = socket;
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.idStore = idStore;
    }

    @Override
    public void run() {
        GetMessage deleteMessage = GetMessage.assembleMessage(Message.getMessageBody(deleteMessageString));
        String state = deleteValue(deleteMessage.getKey(), idStore);
        System.out.println("Delete operation has " + state);
        try {
            UtilsTCP.sendTCPMessage(socket.getOutputStream(), new DeleteMessageReply(state));
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }

    private String deleteValue(String key, ArrayList<String> idStore){
        String path = "";
        int index = -1;
        for (int i = 0; i < idStore.size(); i++){
            if (idStore.get(i).equals(key)) {
                path = folderPath + folderName + "/file_" + key;
                index = i;
                break;
            }
        }

        if (index == -1) { return "failed"; }

        File file = new File(path);
        if (!file.exists() || !file.delete()) { return "failed"; }

        idStore.remove(index);
        return "succeeded";
    }
}
