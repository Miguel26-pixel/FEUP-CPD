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
        this.messageType = messageType;

        this.messageFields.add(new MessageTypeField(messageType));
        this.messageFields.add(new IdField(FieldType.ORIGINID, originId));
    }

    protected Message(String asString) {
        this.messageType = null;
        this.messageFields = new ArrayList<>();

        byte[] delim = new byte[]{CR,LF};

        List<String> split = new ArrayList<>(List.of(asString.split(new String(delim))));

        split.removeIf(s -> s.equals(""));
        split.remove(split.size() - 1);


        for (int i = 0; i < split.size(); i++) {
            String[] fieldString = split.get(i).split(" ");
            FieldType field = MessageField.translateFieldHeader(fieldString[0]);

            if (field != FieldType.INVALID) {
                if (field == FieldType.MESSAGETYPE) {
                    this.addMessageField(new MessageTypeField(MessageTypeField.translateType(fieldString[1])));
                } else {
                    this.addMessageField(new IdField(field, fieldString[1]));
                }
            }
        }
    }

    protected String getFieldValue(FieldType fieldType) {
        for (MessageField field: this.messageFields) {
            if (field.fieldType == fieldType) {
                return field.getValue();
            }
        }

        return null;
    }

    public String getOriginId() {
        return this.getFieldValue(FieldType.ORIGINID);
    }

    protected void addMessageField(MessageField messageField) {
        messageFields.add(messageField);
    }

    public List<Byte> getBody() {
        return body;
    }

    public byte[] assemble() {
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
