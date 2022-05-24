package message;

import message.header.FieldType;
import message.header.MessageField;
import message.header.MessageTypeField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Message {
    public static final byte CR = 0x0d;
    public static final byte LF = 0x0a;

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
        List<Byte> message = new ArrayList<>();

        for (MessageField field: messageFields) {
            message.addAll(field.assemble());
            message.add(CR);
            message.add(LF);
        }

        message.add(CR);
        message.add(LF);

        message.addAll(body);

        message.add(CR);
        message.add(LF);
        message.add(CR);
        message.add(LF);

        byte[] messageBytes = new byte[message.size()];
        for (int i = 0; i < message.size(); i++) {
            messageBytes[i] = message.get(i);
        }

        return messageBytes;
    }

    public static MessageType getMessageType(String message) {
        byte[] delim = new byte[]{CR,LF};
        String[] split = message.split(new String(delim));

        for(String headerLine: split) {
            List<String> splitHeader = List.of(headerLine.split(" "));
            String header = splitHeader.get(0);

            if (MessageField.translateFieldHeader(header) == FieldType.MESSAGETYPE) {
                return MessageTypeField.translateType(splitHeader.get(1));
            }
        }

        return MessageType.INVALID;
    }

    public static String getMessageBody(String message) {
        byte[] delim = new byte[]{CR,LF};
        String[] split = message.split(new String(delim));

        return split[split.length -1];
    }
}
