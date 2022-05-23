package message.header;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageField {
    public FieldType fieldType;

    protected MessageField(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    protected byte translateFieldHeader() {
        return switch (fieldType) {
            case MESSAGETYPE -> 0x00;
            case ORIGINID -> 0x01;
            case DESTID -> 0x02;
        };
    }

    public List<Byte> assemble() {
        List<Byte> field = new ArrayList<>();

        field.add(translateFieldHeader());
        field.add((byte) ' ');
        return field;
    }
}
