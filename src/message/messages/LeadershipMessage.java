package message.messages;

import message.Message;
import message.MessageType;
import node.membership.view.View;

import java.util.ArrayList;
import java.util.List;

public class LeadershipMessage extends Message {
    private final View view;

    public LeadershipMessage(String originId, View view) {
        super(MessageType.LEADERSHIP, originId);
        this.view = view;
        this.buildBody();
    }

    public LeadershipMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        String viewString = split.get(split.size() - 1);
        System.out.println(viewString);
        this.view = new View(viewString);
    }

    @Override
    protected void buildBody() {
        this.body.addAll(this.view.toBytes());
    }
}
