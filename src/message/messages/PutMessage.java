package message.messages;

import message.Message;
import message.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PutMessage extends Message {
    private final File file;
    public PutMessage(File file) {
        super(MessageType.PUT);
        this.file = file;
        this.buildBody();
    }

    @Override
    protected void buildBody() {
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[4];
            while (in.read(bytes) != -1)  {
                for (byte b: bytes) {
                    this.body.add(b);
                }
            }
        } catch (IOException e) {
            System.err.println("Build PutMessage exception: " + e);
        }
    }

    public static PutMessage assembleMessage(String body, String pathname) {
        File recieved_file = new File(pathname);
        return new PutMessage(recieved_file);
    }
}
