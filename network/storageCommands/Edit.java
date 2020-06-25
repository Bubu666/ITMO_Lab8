package network.storageCommands;

import network.User;
import network.human.HumanBeing;
import network.message.Message;
import network.storage.Storage;

public class Edit extends StorageCommand<Message> {

    public final static String helpInfo = "  update id : обновить значение элемента коллекции, id которого равен заданному\n  update id random : (*)\n";

    private final User user;
    private final HumanBeing humanBeing;

    public Edit(User user, HumanBeing humanBeing) {
        super("edit");
        this.user = user;
        this.humanBeing = humanBeing;
    }

    @Override
    public Message execute(Storage storage) {
        return storage.edit(user, humanBeing);
    }
}
