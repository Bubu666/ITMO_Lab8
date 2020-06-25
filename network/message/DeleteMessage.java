package network.message;

public class DeleteMessage extends ChangeCollectionMessage {
    public final int[] id;

    public DeleteMessage(String content, int[] id) {
        super(content);
        this.id = id;
    }
}
