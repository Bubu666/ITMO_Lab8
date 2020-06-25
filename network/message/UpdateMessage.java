package network.message;

import network.human.HumanBeing;

public class UpdateMessage extends ChangeCollectionMessage {
    public final int lastId;
    public final HumanBeing newHumanBeing;

    public UpdateMessage(String content, int lastId, HumanBeing newHumanBeing) {
        super(content);
        this.lastId = lastId;
        this.newHumanBeing = newHumanBeing;
    }
}
