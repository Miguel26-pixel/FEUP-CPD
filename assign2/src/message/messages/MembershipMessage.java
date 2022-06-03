package message.messages;

import message.Message;
import message.MessageType;
import node.membership.log.Log;
import node.membership.view.View;
import node.membership.view.ViewEntry;

public class MembershipMessage extends Message {
    private final static int RECENTENTRIESSIZE = 32;
    private View view;

    public MembershipMessage(View view) {
        super(MessageType.MEMBERSHIP);
        this.view = view;
        this.buildBody();
    }

    public MembershipMessage(String asString) {
        super(MessageType.MEMBERSHIP);

        String[] split = asString.split("\\|");
        String viewString = split[0];
        this.view = new View(viewString);
    }

    protected void buildBody() {
        this.body.addAll(this.view.toBytes());
        this.body.add((byte)'|');
        this.body.addAll(Log.toBytes());
    }

    public View getView() {
        return view;
    }
}
