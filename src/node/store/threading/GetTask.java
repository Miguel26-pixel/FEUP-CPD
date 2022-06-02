package node.store.threading;

import message.Message;
import message.messages.GetMessage;
import message.messages.GetMessageReply;
import utils.UtilsTCP;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GetTask extends Thread {
    private final String getMessageString;
    private final String folderPath;
    private final String folderName;
    private final ArrayList<String> idStore;
    private final Socket socket;

    public GetTask(String getMessageString, Socket socket, String folderPath, String folderName, ArrayList<String> idStore) {
        this.getMessageString = getMessageString;
        this.socket = socket;
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.idStore = idStore;
    }

    @Override
    public void run() {
        GetMessage getMessage = GetMessage.assembleMessage(Message.getMessageBody(getMessageString));
        File file = getValue(getMessage.getKey());
        System.out.println((file == null) ? "File does not exists" : "File obtained with success");

        try {
            OutputStream outputStream = socket.getOutputStream();
            GetMessageReply m = new GetMessageReply(file);
            UtilsTCP.sendTCPMessage(outputStream, m);
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP Exception: " + e);
        }
    }

    public File getValue(String key){
        for (String existingKey : idStore) {
            if (existingKey.equals(key)) {
                File file = new File(folderPath + folderName + "/file_" + key);
                if (file.exists() && file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }
}
