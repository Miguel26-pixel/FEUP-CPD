package message.messages;

import message.Message;
import message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class JoinReplyMessage extends Message {
    private final String counter;

    public JoinReplyMessage(String originId, int counter) {
        super(MessageType.JOIN_REPLY, originId);
        this.counter = Integer.toString(counter);
        this.buildBody();
    }

    public JoinReplyMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        this.counter = split.get(split.size() - 1);
    }

    public int getCounter() {
        return Integer.parseInt(counter);
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();
        for (byte b : counter.getBytes()) {
            this.body.add(b);
        }
    }
}
