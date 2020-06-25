package network.message;

import network.human.HumanBeing;

public class CollectionMessage extends Message {
    public final HumanBeing[] humanBeings;
    public final boolean tellEveryone;

    public CollectionMessage(String content, HumanBeing[] humanBeings, boolean tellEveryone) {
        super(content);
        this.humanBeings = humanBeings;
        this.tellEveryone = tellEveryone;
    }
}
