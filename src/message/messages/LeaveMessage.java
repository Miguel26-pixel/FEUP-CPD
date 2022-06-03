package message.messages;

import message.Message;
import message.MessageType;
import message.header.FieldType;
import message.header.IdField;
import message.header.MessageField;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaveMessage extends Message {
    private final String counter;

    public LeaveMessage(int counter, String originId) {
        super(MessageType.LEAVE, originId);
        this.counter = Integer.toString(counter);

        this.buildBody();
    }

    public LeaveMessage(String asString) {
        super(asString);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));

        String body = split.get(split.size() - 1);
        split.remove(split.size() - 1);

        int bodyLen = Integer.parseInt(this.getFieldValue(FieldType.BODYLENGHT));

        List<String> params = new ArrayList<>(List.of(body.split(" ")));
        this.counter = params.get(0).substring(0,bodyLen);
    }

    public Integer getCounter() {
        return Integer.parseInt(this.counter);
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();
        for (byte b : counter.getBytes()) {
            this.body.add(b);
        }
    }
}
