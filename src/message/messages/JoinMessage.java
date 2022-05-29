package message.messages;

import message.Message;
import message.MessageType;
import message.header.FieldType;
import message.header.MessageField;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class JoinMessage extends Message {
    private final String counter;
    private final String port;

    public JoinMessage(int counter, int port, String originId) {
        super(MessageType.JOIN, originId);
        this.counter = Integer.toString(counter);
        this.port = Integer.toString(port);

        this.buildBody();
    }

    public JoinMessage(String asString) {
        super(MessageType.JOIN);

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        split.remove(split.size() - 1);

        String body = split.get(split.size() - 1);
        split.remove(split.size() - 1);

        for (int i = 0; i < split.size() / 2; i += 2) {
            if (MessageField.translateFieldHeader(split.get(i)) == FieldType.ORIGINID) {
                String originId = split.get(i + 1);

                this.setOriginId(originId);
            }
        }

        List<String> params = new ArrayList<>(List.of(body.split(" ")));

        this.counter = params.get(0);
        this.port = params.get(1);
    }

    public Integer getPort() {
        return Integer.parseInt(port);
    }

    public Integer getCounter() {
        return Integer.parseInt(counter);
    }

    @Override
    protected void buildBody() {
        this.body = new ArrayList<>();

        for (byte b : counter.getBytes()) {
            this.body.add(b);
        }

        this.body.add((byte) ' ');

        for (byte b : port.getBytes()) {
            this.body.add(b);
        }
    }
}
