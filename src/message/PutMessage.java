package message;

import message.Message;
import message.MessageType;

public class PutMessage extends Message {

    protected PutMessage(MessageType messageType) {
        super(messageType);
    }

    @Override
    protected void buildBody() {

    }
}
