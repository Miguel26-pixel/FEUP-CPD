package node.membership.message;

import node.membership.view.View;

public class MembershipMessage extends Message {
    private final static int RECENTENTRIESSIZE = 32;
    View log;

    public MembershipMessage(View log) {
        super(MessageType.MEMBERSHIP);
        this.log = log;
        this.buildBody();
    }

    private void buildBody() {
        this.body = log.getMostRecentEntries(RECENTENTRIESSIZE).toBytes();
    }
}
