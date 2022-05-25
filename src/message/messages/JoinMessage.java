package message.messages;

import message.Message;
import message.MessageType;
import message.header.FieldType;
import message.header.IdField;
import message.header.MessageField;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class JoinMessage extends Message {
    private final Integer counter;
    private final Integer port;
    private String ip;

    public JoinMessage(int counter, int port, String ip) {
        super(MessageType.JOIN);
        this.counter = counter;
        this.port = port;
        this.ip = ip;

        this.addMessageField(new IdField(FieldType.ORIGINID, ip));

        this.buildBody();
    }

    public JoinMessage(String asString) {
        super(MessageType.JOIN);

        List<String> split = new ArrayList<>(List.of(asString.split(CR.toString() + LF.toString())));

        split.removeIf(s -> s.equals(""));

        String body = split.get(split.size() - 1);
        split.remove(split.size() - 1);

        for (int i = 0; i < split.size() / 2; i += 2) {
            if (MessageField.translateFieldHeader(split.get(i)) == FieldType.ORIGINID) {
                this.ip = split.get(i + 1);
                this.addMessageField(new IdField(FieldType.ORIGINID, ip));
            }
        }

        List<String> params = new ArrayList<>(List.of(body.split(" ")));

        this.counter = Integer.parseInt(params.get(0));
        this.port = Integer.parseInt(params.get(1));
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
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

        this.body.add((byte) ' ');

        bytes = ByteBuffer.allocate(4).putInt(port).array();

        for (byte b : bytes) {
            this.body.add(b);
        }
    }
}
