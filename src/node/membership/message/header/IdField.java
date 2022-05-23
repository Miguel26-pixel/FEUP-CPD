package node.membership.message.header;

import java.util.List;

public class IdField extends MessageField {
    String id;

    public IdField(FieldType fieldType, String id) {
        super(fieldType);
        this.id = id;
    }

    @Override
    public List<Byte> assemble() {
        List<Byte> field = super.assemble();

        for (Byte b: id.getBytes()) {
            field.add(b);
        }

        return field;
    }
}
