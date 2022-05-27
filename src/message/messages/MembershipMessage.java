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
        this.view = extractString(asString);
    }

    private static View extractString(String asString) {
        View view = new View();

        String[] split = asString.split("\\|");
        for(String entryString: split[0].split("\n")) {
            String[] entry = entryString.split(";");
            String id = entry[0];
            String address = entry[1];
            String port = entry[2];
            String counter = entry[3];
            String epoch = entry[4];

            view.addEntry(id, new ViewEntry(address, port, Integer.parseInt(counter), Integer.parseInt(epoch)));
        }

        return view;
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
