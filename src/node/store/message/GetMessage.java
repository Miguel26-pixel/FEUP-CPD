package node.store.message;

import message.Message;
import message.MessageType;

public class GetMessage extends Message {

    protected GetMessage(MessageType messageType) {
        super(messageType);
    }

    @Override
    protected void buildBody() {

    }
}
