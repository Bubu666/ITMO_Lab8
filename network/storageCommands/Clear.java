package network.storageCommands;

import network.User;
import network.message.DeleteMessage;
import network.message.Message;
import network.storage.Storage;

/**
 * Команда "clear"
 */
public class Clear extends StorageCommand<Message> {
    private User user;
    /**
     * Информация о использовании команды
     */
    public final static String helpInfo = "  clear : очистить коллекцию\n";

    public Clear(User user) {
        super("clear");
        this.user = user;
    }

    /**
     * Реализация команды
     */
    @Override
    public Message execute(Storage storage) {
        return storage.clear(user);
    }
}
