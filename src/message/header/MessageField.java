package message.header;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageField {
    public FieldType fieldType;

    protected MessageField(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    protected String translateFieldHeader() {
        return switch (fieldType) {
            case MESSAGETYPE -> "messagetype";
            case ORIGINID -> "originid";
            case DESTID -> "destid";
            case INVALID -> "";
        };
    }

    public static FieldType translateFieldHeader(String fieldType) {
        return switch (fieldType) {
            case "messagetype" -> FieldType.MESSAGETYPE;
            case "originid" -> FieldType.ORIGINID;
            case "destid" -> FieldType.DESTID;
            default -> FieldType.INVALID;
        };
    }

    public List<Byte> assemble() {
        List<Byte> field = new ArrayList<>();
        for (Byte b: translateFieldHeader().getBytes()) {
            field.add(b);
        }
        field.add((byte)' ');
        return field;
    }
}
