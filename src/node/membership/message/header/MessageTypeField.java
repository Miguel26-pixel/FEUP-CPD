package node.membership.message.header;

import node.membership.message.MessageType;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MessageTypeField extends MessageField {
    public MessageType messageType;

    public MessageTypeField(MessageType messageType) {
        super(FieldType.MESSAGETYPE);
        this.messageType = messageType;
    }

    public String translateType() {
        return switch (messageType) {
            case JOIN -> "join";
            case LEAVE -> "leave";
            case MEMBERSHIP -> "membership";
            case INVALID -> "invalid";
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
