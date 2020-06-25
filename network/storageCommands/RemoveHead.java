package network.storageCommands;

import network.User;
import network.message.DeleteMessage;
import network.message.Message;
import network.storage.Storage;

/**
 * Команда "remove_head"
 */
public class RemoveHead extends StorageCommand<Message> {
    private User user;
    /**
     * Информация о использовании команды
     */
    public final static String helpInfo = "  remove_head : вывести первый элемент коллекции и удалить его\n";

    public RemoveHead(User user) {
        super("remove_head");
        this.user = user;
    }

    /**
     * Реализация команды
     */
    @Override
    public Message execute(Storage storage) {
        return storage.remove_head(user);
    }
}
