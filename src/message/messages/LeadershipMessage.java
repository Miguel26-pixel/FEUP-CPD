package message.messages;

import message.Message;
import message.MessageType;
import node.membership.view.View;

import java.util.ArrayList;
import java.util.List;

public class LeadershipMessage extends Message {
    private final View view;
    private final String leaderId;

    public LeadershipMessage(String originId, String leaderId, View view) {
        super(MessageType.LEADERSHIP, originId);
        this.leaderId = leaderId;
        this.view = view;
        this.buildBody();
    }

    public LeadershipMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        String[] body = split.get(split.size() - 1).split("\\|");
        this.leaderId = body[0];
        this.view = new View(body[1]);
    }

    public View getView() {
        return view;
    }

    public String getLeaderId() {
        return leaderId;
    }

    @Override
    protected void buildBody() {
        for (byte b: this.leaderId.getBytes()) {
            this.body.add(b);
        }
        this.body.add((byte)'|');
        this.body.addAll(this.view.toBytes());
    }
}
