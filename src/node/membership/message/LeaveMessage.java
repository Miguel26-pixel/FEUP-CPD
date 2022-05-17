package node.membership.message;

import java.util.List;

public class LeaveMessage extends Message {
    public LeaveMessage() {
        super(MessageType.JOIN);
    }
}
