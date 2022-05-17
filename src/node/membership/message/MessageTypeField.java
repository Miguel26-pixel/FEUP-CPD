package node.membership.message;

import java.util.ArrayList;
import java.util.List;

public class MessageTypeField extends MessageField {
    public MessageType messageType;

    public MessageTypeField(FieldType fieldType, MessageType messageType) {
        super(fieldType);
        this.messageType = messageType;
    }

    private char translateType() {
        return switch (messageType) {
            case JOIN -> 0x00;
            case LEAVE -> 0x01;
            case MEMBERSHIP -> 0x02;
        };
    }

    @Override
    public List<char> assemble() {
        List<char> field = super.assemble();
        field.add(translateType());
        return field;
    }
}
