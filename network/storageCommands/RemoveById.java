package network.storageCommands;

import network.User;
import network.message.DeleteMessage;
import network.message.Message;
import network.storage.Storage;

/**
 * Команда "remove_by_id"
 */
public class RemoveById extends StorageCommand<Message> {
    private User user;
    /**
     * id объекта
     */
    private int id;

    /**
     * Информация о использовании команды
     */
    public final static String helpInfo = "  remove_by_id id : удалить элемент из коллекции по его id\n";

    /**
     * Принимает id объекта
     * @param user
     * @param id Id объекта
     */
    public RemoveById(User user, int id) {
        super("remove_by_id");
        this.user = user;
        this.id = id;
    }

    /**
     * Реализация команды
     */
    @Override
    public Message execute(Storage storage) {
        return storage.remove_by_id(id, user);
    }
}
