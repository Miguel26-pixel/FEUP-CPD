package message;

import message.header.MessageField;
import message.header.MessageTypeField;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
    private static final Byte CR = 0x0d;
    private static final Byte LF = 0x0a;

    private final List<MessageField> messageFields;
    protected List<Byte> body;

    protected Message(MessageType messageType) {
        this.messageFields = new ArrayList<>();
        this.body = new ArrayList<Byte>();

        this.messageFields.add(new MessageTypeField(messageType));
    }

    protected abstract void buildBody();

    protected void addMessageField(MessageField messageField) {
        messageFields.add(messageField);
    }

    public List<Byte> getBody() {
        return body;
    }

    public byte[] assemble() {
        List<Byte> message = new ArrayList<Byte>();

        for (MessageField field: messageFields) {
            message.addAll(field.assemble());
            message.add(CR);
            message.add(LF);
        }

        message.add(CR);
        message.add(LF);

        message.addAll(body);

        byte[] messageBytes = new byte[message.size()];
        for (int i = 0; i < message.size(); i++) {
            messageBytes[i] = message.get(i);
        }

        return messageBytes;
    }

    //1:5,1237;2:8,129381 Log message data format
}
