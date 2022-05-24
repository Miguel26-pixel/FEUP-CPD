package message;

public class GetMessage extends Message {
    private final String key;

    public GetMessage(String key) {
        super(MessageType.GET);
        this.key = key;
    }

    @Override
    protected void buildBody() {

    }
}
