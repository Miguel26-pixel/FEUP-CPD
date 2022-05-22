package node.membership.message;

import node.membership.message.header.FieldType;
import node.membership.message.header.MessageField;
import node.membership.message.header.MessageTypeField;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class JoinMessage extends Message {
    private final Integer counter;
    private final Integer port;

    public JoinMessage(int counter, int port) {
        super(MessageType.JOIN);
        this.counter = counter;
        this.port = port;

        this.buildBody();
    }

    public JoinMessage(String asString) {
        super(MessageType.JOIN);

        List<String> split = new ArrayList<>(List.of(asString.split(CR.toString() + LF.toString())));

        split.removeIf(s -> s.equals(""));

        String body = split.get(split.size() - 1);
        List<String> params = new ArrayList<>(List.of(body.split(" ")));

        this.counter = Integer.parseInt(params.get(0));
        this.port = Integer.parseInt(params.get(1));
    }

    private void buildBody() {
        this.body = new ArrayList<>();
        byte[] bytes = ByteBuffer.allocate(4).putInt(counter).array();

        for (byte b : bytes) {
            this.body.add(b);
        }

        this.body.add((byte) ' ');

        bytes = ByteBuffer.allocate(4).putInt(port).array();

        for (byte b : bytes) {
            this.body.add(b);
        }
    }
}
