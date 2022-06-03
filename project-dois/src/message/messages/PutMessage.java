package message.messages;

import message.Message;
import message.MessageType;
import utils.UtilsFile;

import java.io.File;

public class PutMessage extends Message {
    private final File file;
    public PutMessage(File file) {
        super(MessageType.PUT);
        this.file = file;
        this.buildBody();
    }

    @Override
    protected void buildBody() {
        UtilsFile.fileToBytes(file,body);
    }

    public static PutMessage assembleMessage(String body, String filepath) {
        return new PutMessage(UtilsFile.stringToFile(body,filepath));
    }
}
