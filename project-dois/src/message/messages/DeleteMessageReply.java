package message.messages;

import message.Message;
import message.MessageType;

import java.util.ArrayList;

public class DeleteMessageReply extends Message {
    private final String ackMessage;

    public DeleteMessageReply(String ackMessage) {
        super(MessageType.DELETE_REPLY);
        this.ackMessage = ackMessage;
        this.buildBody();
    }

    public String getAckMessage() {
        return ackMessage;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : ackMessage.getBytes()) {
            body.add(b);
        }
    }

    public static DeleteMessageReply assembleMessage(String body) {
        return new DeleteMessageReply(body);
    }
}
