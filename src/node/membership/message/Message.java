package node.membership.message;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;

    private final List<MessageField> messageFields;
    protected List<byte> body;

    protected Message(MessageType messageType) {
        this.messageFields = new ArrayList<>();
        this.body = new ArrayList<byte>();

        this.messageFields.add(new MessageTypeField(FieldType.MESSAGETYPE, messageType));
    }

    protected void addMessageField(MessageField messageField) {
        messageFields.add(messageField);
    }

    public List<byte> getBody() {
        return body;
    }

    public List<byte> assemble() {
        List<byte> message = new ArrayList<byte>();

        for (MessageField field: messageFields) {
            message.addAll(field.assemble());
            message.add(CR);
            message.add(LF);
        }

        message.add(CR);
        message.add(LF);

        message.addAll(body);

        return message;
    }

    //1:5,1237;2:8,129381 Log message data format
}
