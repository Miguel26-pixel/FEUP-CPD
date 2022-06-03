package message.messages;

import message.Message;
import message.MessageType;

import java.util.ArrayList;

public class ForceDeleteMessage extends Message {
    private final String key;

    public ForceDeleteMessage(String key) {
        super(MessageType.FORCE_DELETE);
        this.key = key;
        this.buildBody();
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : key.getBytes()) {
            body.add(b);
        }
    }

    public static DeleteMessage assembleMessage(String body) {
        return new DeleteMessage(body);
    }
}
