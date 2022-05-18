package node.membership.message;

import node.Node;
import node.membership.log.Log;
import node.membership.log.LogEntry;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MembershipMessage extends Message {
    Log log;

    public MembershipMessage(Log log, List<Node> nodes) {
        super(MessageType.MEMBERSHIP);
        this.log = log;
    }

    private void buildBody() {
        this.body = log.toBytes();
    }
}
