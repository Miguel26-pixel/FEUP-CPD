package message.messages;

import message.Message;
import message.MessageType;
import node.membership.view.View;

import java.util.ArrayList;
import java.util.List;

public class CoupMessage extends Message {
    private final String couperId;

    public CoupMessage(String originId, String couperId) {
        super(MessageType.COUP, originId);

        this.couperId = couperId;
        this.buildBody();
    }

    public CoupMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        this.couperId = split.get(split.size() - 1);
    }

    public String getCouperId() {
        return couperId;
    }

    @Override
    protected void buildBody() {
        for (byte b: this.couperId.getBytes()) {
            this.body.add(b);
        }
    }
}
