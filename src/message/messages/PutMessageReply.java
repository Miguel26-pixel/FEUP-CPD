package message.messages;

import message.Message;
import message.MessageType;

import java.util.ArrayList;

public class PutMessageReply extends Message {
    private final String key;
    public PutMessageReply(String key) {
        super(MessageType.PUT_REPLY);
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
}
