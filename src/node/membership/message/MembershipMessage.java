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
        this.body = new ArrayList<>();

        for (Map.Entry<String, LogEntry> logEntry : log.getEntries().entrySet()) {
            byte[] key = logEntry.getKey().getBytes();
            for (byte b: key) {
                this.body.add(b);
            }

            this.body.add((byte) ';');

            byte[] counter = ByteBuffer.allocate(4).putInt(logEntry.getValue().getCounter()).array();

            for (byte b : counter) {
                this.body.add(b);
            }

            this.body.add((byte) ';');

            byte[] epoch = ByteBuffer.allocate(4).putInt(logEntry.getValue().getEpoch()).array();

            for (byte b : epoch) {
                this.body.add(b);
            }

            this.body.add((byte) ' ');
        }
    }
}
