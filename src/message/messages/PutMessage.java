package message.messages;

import message.Message;
import message.MessageType;

import java.io.File;

public class PutMessage extends Message {
    private final File file;
    public PutMessage(File file) {
        super(MessageType.PUT);
        this.file = file;
    }

    @Override
    protected void buildBody() {

    }

    public static PutMessage assembleMessage(byte[] bytes, String pathname) {
        File recieved_file = new File(pathname);
        return new PutMessage(recieved_file);
    }
}
