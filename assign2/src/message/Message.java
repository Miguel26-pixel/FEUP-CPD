package message;

import message.header.FieldType;
import message.header.IdField;
import message.header.MessageField;
import message.header.MessageTypeField;

import java.util.ArrayList;
import java.util.List;

public abstract class Message {
    public static final Byte CR = 0x0d;
    public static final Byte LF = 0x0a;

    protected abstract void buildBody();
    private final List<MessageField> messageFields;
    private final MessageType messageType;
    private String originId;
    protected List<Byte> body;

    protected Message(MessageType messageType) {
        this.messageType = messageType;
        this.messageFields = new ArrayList<>();
        this.body = new ArrayList<Byte>();

        this.messageFields.add(new MessageTypeField(messageType));
    }

    protected Message(MessageType messageType, String originId) {
        this.messageFields = new ArrayList<>();
        this.body = new ArrayList<Byte>();
        this.originId = originId;
        this.messageType = messageType;

        this.messageFields.add(new MessageTypeField(messageType));
    }

    protected void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOriginId() {
        return originId;
    }

    protected void addMessageField(MessageField messageField) {
        messageFields.add(messageField);
    }

    public List<Byte> getBody() {
        return body;
    }

    public byte[] assemble() {
        this.setOriginIdField();
        this.setBodyLenghtField();

        List<Byte> message = new ArrayList<>();

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

    private void setOriginIdField() {
        if (this.originId != null) {
            this.addMessageField(new IdField(FieldType.ORIGINID, originId));
        }
    }

    private void setBodyLenghtField() {
        if (this.body != null) {
            this.addMessageField(new IdField(FieldType.BODYLENGHT, Integer.toString(this.body.size())));
        }
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

    public MessageType getMessageType() {
        return messageType;
    }
}
