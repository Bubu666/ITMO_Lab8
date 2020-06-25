package network.message;

import network.human.HumanBeing;

public class AddMessage extends ChangeCollectionMessage {
    public final HumanBeing[] humanBeings;

    public AddMessage(String content, HumanBeing[] humanBeings) {
        super(content);
        this.humanBeings = humanBeings;
    }
}
