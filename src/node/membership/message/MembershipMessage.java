package node.membership.message;

import node.membership.log.Log;

public class MembershipMessage extends Message {
    private final static int RECENTENTRIESSIZE = 32;
    Log log;

    public MembershipMessage(Log log) {
        super(MessageType.MEMBERSHIP);
        this.log = log;
        this.buildBody();
    }

    private void buildBody() {
        this.body = log.getMostRecentEntries(RECENTENTRIESSIZE).toBytes();
    }
}
