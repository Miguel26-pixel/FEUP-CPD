package message.messages;

import message.Message;
import message.MessageType;

public class DeleteMessageReply extends Message {
    private final String ackMessage;

    protected DeleteMessageReply(String ackMessage) {
        super(MessageType.DELETE_REPLY);
        this.ackMessage = ackMessage;
    }

    @Override
    protected void buildBody() {

    }
}
