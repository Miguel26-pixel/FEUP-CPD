package message;

import node.membership.message.header.MessageField;
import node.membership.message.header.MessageTypeField;

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

    protected void addMessageField(MessageField messageField) {
        messageFields.add(messageField);
    }

    public List<Byte> getBody() {
        return body;
    }

    public List<Byte> assemble() {
        List<Byte> message = new ArrayList<Byte>();

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
