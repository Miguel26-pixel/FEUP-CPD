package message;

import message.header.FieldType;
import message.header.MessageField;
import message.header.MessageTypeField;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
    public static final Byte CR = 0x0d;
    public static final Byte LF = 0x0a;

    protected abstract void buildBody();
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

    public static MessageType getMessageType(String message) {
        List<String> split = new ArrayList<>(List.of(message.split(CR.toString() + LF.toString())));

        split.removeIf(s -> s.equals(""));
        split.remove(split.size() - 1);

        for(String headerLine: split) {
            List<String> splitHeader = List.of(headerLine.split(" "));
            String header = splitHeader.get(0);

            if (MessageField.translateFieldHeader(header) == FieldType.MESSAGETYPE) {
                return MessageTypeField.translateType(splitHeader.get(1));
            }
        }

        return MessageType.INVALID;
    }

    //1:5,1237;2:8,129381 Log message data format
}
