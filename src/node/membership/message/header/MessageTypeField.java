package node.membership.message.header;

import node.membership.message.MessageType;

import java.util.List;

public class MessageTypeField extends MessageField {
    public MessageType messageType;

    public MessageTypeField(FieldType fieldType, MessageType messageType) {
        super(fieldType);
        this.messageType = messageType;
    }

    private byte translateType() {
        return switch (messageType) {
            case JOIN -> 0x00;
            case LEAVE -> 0x01;
            case MEMBERSHIP -> 0x02;
        };
    }

    @Override
    public List<Byte> assemble() {
        List<Byte> field = super.assemble();
        field.add(translateType());
        return field;
    }
}
