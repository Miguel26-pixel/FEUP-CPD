package message;

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
}
