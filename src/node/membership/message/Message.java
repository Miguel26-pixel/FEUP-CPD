package node.membership.message;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
    protected static final char CR = 0x0d;
    protected static final char LF = 0x0a;

    public List<MessageField> messageFields;

    protected Message(MessageType messageType) {
        this.messageFields = new ArrayList<>();
        this.messageFields.add(new MessageTypeField(FieldType.MESSAGETYPE, messageType));
    }

    public List<char> assemble() {
        List<char> message = new ArrayList<char>();

        for (MessageField field: messageFields) {
            message.addAll(field.assemble());
            message.add(CR);
            message.add(LF);
        }

        message.add(CR);
        message.add(LF);

        return message;
    }

    //1:5,1237;2:8,129381 Log message data format
}
