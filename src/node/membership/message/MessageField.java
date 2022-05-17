package node.membership.message;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageField {
    public FieldType fieldType;

    protected MessageField(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    protected char translateFieldHeader() {
        return switch (fieldType) {
            case MESSAGETYPE -> 0x00;
            case ORIGINID -> 0x01;
            case DESTID -> 0x02;
            case COUNTER -> 0x03;
        };
    }

    public List<char> assemble() {
        List<char> field = new ArrayList<char>();

        field.add(translateFieldHeader());
        field.add(' ');
        return field;
    }
}
