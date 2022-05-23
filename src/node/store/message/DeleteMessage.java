package node.store.message;

import message.Message;
import message.MessageType;

public class DeleteMessage extends Message {

    protected DeleteMessage(MessageType messageType) {
        super(messageType);
    }

    @Override
    protected void buildBody() {

    }
}
