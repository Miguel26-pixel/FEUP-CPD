package message.messages;

import message.Message;
import message.MessageType;

public class GetMessageReply extends Message {
    public GetMessageReply() {
        super(MessageType.GET_REPLY);
    }

    @Override
    protected void buildBody() {

    }
}
