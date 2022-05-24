package message.header;


import message.MessageType;

import java.util.List;

public class MessageTypeField extends MessageField {
    public MessageType messageType;

    public MessageTypeField(MessageType messageType) {
        super(FieldType.MESSAGETYPE);
        this.messageType = messageType;
    }

    private String translateType() {
        return switch (messageType) {
            case JOIN -> "join";
            case LEAVE -> "leave";
            case MEMBERSHIP -> "membership";
            case PUT -> "put";
            case GET -> "get";
            case DELETE -> "delete";
            case INVALID -> "invalid";
        };
    }

    public static MessageType translateType(String typeAsString) {
        return switch (typeAsString) {
            case "join" -> MessageType.JOIN;
            case "leave" -> MessageType.LEAVE;
            case "membership" -> MessageType.MEMBERSHIP;
            case "put" -> MessageType.PUT;
            case "get" -> MessageType.GET;
            case "delete" -> MessageType.DELETE;
            case "invalid" -> MessageType.INVALID;
        };
    }

    @Override
    public List<Byte> assemble() {
        List<Byte> field = super.assemble();
        for (Byte b: translateType().getBytes()) {
            field.add(b);
        }
        return field;
    }
}
