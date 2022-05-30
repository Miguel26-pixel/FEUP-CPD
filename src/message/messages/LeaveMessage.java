package message.messages;

import message.Message;
import message.MessageType;
import message.header.FieldType;
import message.header.IdField;
import message.header.MessageField;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LeaveMessage extends Message {
    private final String counter;

    public LeaveMessage(int counter, String originId) {
        super(MessageType.LEAVE, originId);
        this.counter = Integer.toString(counter);

        this.buildBody();
    }

    public LeaveMessage(String asString) {
        super(MessageType.LEAVE);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        split.remove(split.size() - 1);

        String body = split.get(split.size() - 1);
        split.remove(split.size() - 1);

        for (int i = 0; i < split.size(); i++) {
            String[] field = split.get(i).split(" ");
            if (MessageField.translateFieldHeader(field[0]) == FieldType.ORIGINID) {
                String originId = field[1];

                this.setOriginId(originId);
            }
        }

        List<String> params = new ArrayList<>(List.of(body.split(" ")));

        this.counter = params.get(0);
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
