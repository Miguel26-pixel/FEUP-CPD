package message.messages;

import message.Message;
import message.MessageType;

public class CoupMessage extends Message {
    public CoupMessage(String originId) {
        super(MessageType.COUP, originId);
    }

    public CoupMessage(String asString, boolean dummy) {
        super(asString);
    }

    @Override
    protected void buildBody() {}
}
