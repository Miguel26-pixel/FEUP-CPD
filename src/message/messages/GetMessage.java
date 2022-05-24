package message.messages;

import message.Message;
import message.MessageType;

import java.io.File;
import java.util.ArrayList;

public class GetMessage extends Message {
    private final String key;

    public GetMessage(String key) {
        super(MessageType.GET);
        this.key = key;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : key.getBytes()) {
            body.add(b);
        }
    }

    public static GetMessage assembleMessage(byte[] bytes) {
        return new GetMessage("");
    }
}
