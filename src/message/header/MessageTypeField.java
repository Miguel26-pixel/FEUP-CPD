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
            case LEADERSHIP -> "leadership";
            case COUP -> "coup";
            case PUT -> "put";
            case PUT_REPLY -> "put_reply";
            case FORCE_PUT -> "force_put";
            case GET -> "get";
            case GET_REPLY -> "get_reply";
            case DELETE -> "delete";
            case DELETE_REPLY -> "delete_reply";
            default -> "invalid";
        };
    }

    public static MessageType translateType(String typeAsString) {
        return switch (typeAsString) {
            case "join" -> MessageType.JOIN;
            case "leave" -> MessageType.LEAVE;
            case "membership" -> MessageType.MEMBERSHIP;
            case "leadership" -> MessageType.LEADERSHIP;
            case "coup" -> MessageType.COUP;
            case "put" -> MessageType.PUT;
            case "put_reply" -> MessageType.PUT_REPLY;
            case "force_put" -> MessageType.FORCE_PUT;
            case "get" -> MessageType.GET;
            case "get_reply" -> MessageType.GET_REPLY;
            case "delete" -> MessageType.DELETE;
            case "delete_reply" -> MessageType.DELETE_REPLY;

            default -> MessageType.INVALID;
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

    @Override
    public String getValue() {
        return this.translateType();
    }
}
