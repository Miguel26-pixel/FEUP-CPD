package message.messages;

import message.Message;
import message.MessageType;
import node.membership.log.Log;
import node.membership.view.View;

import java.util.ArrayList;
import java.util.List;

public class MembershipMessage extends Message {
    private final static int RECENTENTRIESSIZE = 32;
    private View view;

    public MembershipMessage(View view) {
        super(MessageType.MEMBERSHIP);
        this.view = view;
        this.buildBody();
    }

    public MembershipMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        String body = split.get(split.size() - 1);
        String viewString = body.split("\\|")[0];
        this.view = new View(viewString);
    }

    protected void buildBody() {
        this.body.addAll(this.view.toBytes());
        this.body.add((byte)'|');
        this.body.addAll(this.view.getLog().toBytes());
    }

    public View getView() {
        return view;
    }
}
