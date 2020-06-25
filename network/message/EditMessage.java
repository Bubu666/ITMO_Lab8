package network.message;

import network.human.HumanBeing;

public class EditMessage extends ChangeCollectionMessage {
    public final HumanBeing humanBeing;

    public EditMessage(String content, HumanBeing humanBeing) {
        super(content);
        this.humanBeing = humanBeing;
    }
}