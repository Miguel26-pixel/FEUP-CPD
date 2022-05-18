package node.membership.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LeaveMessage extends Message {
    private final Integer counter;

    public LeaveMessage(int counter) {
        super(MessageType.JOIN);
        this.counter = counter;
    }

    private void buildBody() {
        this.body = new ArrayList<>();
        byte[] bytes = ByteBuffer.allocate(4).putInt(counter).array();

        for (byte b : bytes) {
            this.body.add(b);
        }
    }
}
