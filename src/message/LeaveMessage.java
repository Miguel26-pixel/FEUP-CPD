package message;

import message.Message;
import message.MessageType;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LeaveMessage extends Message {
    private final Integer counter;

    public LeaveMessage(int counter) {
        super(MessageType.JOIN);
        this.counter = counter;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();
        byte[] bytes = ByteBuffer.allocate(4).putInt(counter).array();

        for (byte b : bytes) {
            this.body.add(b);
        }
    }
}
