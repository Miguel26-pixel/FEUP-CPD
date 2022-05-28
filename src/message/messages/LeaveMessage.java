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
    private final Integer counter;

    public LeaveMessage(int counter, String originId) {
        super(MessageType.LEAVE, originId);
        this.counter = counter;

        this.buildBody();
    }

    public LeaveMessage(String asString) {
        super(MessageType.LEAVE);

        List<String> split = new ArrayList<>(List.of(asString.split(CR.toString() + LF.toString())));

        split.removeIf(s -> s.equals(""));

        String body = split.get(split.size() - 1);
        split.remove(split.size() - 1);

        for (int i = 0; i < split.size() / 2; i += 2) {
            if (MessageField.translateFieldHeader(split.get(i)) == FieldType.ORIGINID) {
                String originId = split.get(i + 1);

                this.setOriginId(originId);
            }
        }

        List<String> params = new ArrayList<>(List.of(body.split(" ")));

        this.counter = Integer.parseInt(params.get(0));
    }

    public Integer getCounter() {
        return counter;
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();
        byte[] bytes = ByteBuffer.allocate(4).putInt(counter).array();

        for (byte b : bytes) {
            this.body.add(b);
        }
    }
}
