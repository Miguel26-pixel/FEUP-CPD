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
        this.buildBody();
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : key.getBytes()) {
            this.body.add(b);
        }
        System.out.println(this.body);
    }

    public static GetMessage assembleMessage(String body) {
        return new GetMessage(body);
    }
}
