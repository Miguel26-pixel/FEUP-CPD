package message;

public class DeleteMessage extends Message {
    private final String key;

    public DeleteMessage(String key) {
        super(MessageType.DELETE);
        this.key = key;
    }

    @Override
    protected void buildBody() {

    }
}
