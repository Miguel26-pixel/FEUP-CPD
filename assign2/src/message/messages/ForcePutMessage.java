package message.messages;

import message.Message;
import message.MessageType;
import utils.UtilsFile;

import java.io.File;

public class ForcePutMessage extends Message {
    private final File file;

    public ForcePutMessage(File file) {
        super(MessageType.FORCE_PUT);
        this.file = file;
        this.buildBody();
    }

    @Override
    protected void buildBody() {
        UtilsFile.fileToBytes(file,body);
    }

    public static ForcePutMessage assembleMessage(String body, String filepath) {
        return new ForcePutMessage(UtilsFile.stringToFile(body,filepath));
    }
}
