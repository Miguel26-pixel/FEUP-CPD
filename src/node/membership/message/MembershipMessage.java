package node.membership.message;

import node.membership.log.Log;
import node.membership.view.View;

public class MembershipMessage extends Message {
    private final static int RECENTENTRIESSIZE = 32;
    View view;

    public MembershipMessage(View view) {
        super(MessageType.MEMBERSHIP);
        this.view = view;
        this.buildBody();
    }

    private void buildBody() {
        this.body.addAll(this.view.toBytes());
        this.body.add((byte)'|');
        this.body.addAll(Log.toBytes());
    }
}
