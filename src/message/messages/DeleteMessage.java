package message.messages;

import message.Message;
import message.MessageType;

import java.util.ArrayList;

public class DeleteMessage extends Message {
    private final String key;

    public DeleteMessage(String key) {
        super(MessageType.DELETE);
        this.key = key;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : key.getBytes()) {
            body.add(b);
        }
    }

    public static DeleteMessage assembleMessage(byte[] bytes) {
        return new DeleteMessage("");
    }
}
