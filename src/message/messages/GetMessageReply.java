package message.messages;

import message.Message;
import message.MessageType;
import utils.UtilsFile;

import java.io.File;

public class GetMessageReply extends Message {
    private final File file;
    public GetMessageReply(File file) {
        super(MessageType.GET_REPLY);
        this.file = file;
        this.buildBody();
    }

    public File getFile() {
        return file;
    }

    @Override
    protected void buildBody() {
        UtilsFile.fileToBytes(file, body);
    }

    public static GetMessageReply assembleMessage(String body, String filepath) {
        return new GetMessageReply(UtilsFile.stringToFile(body,filepath));
    }

    public static boolean isBodyEmpty(String message) {
        byte[] delim = new byte[]{CR,LF};
        String[] split = message.split(new String(delim));

        return split.length == 1;
    }
}
