package network.storage;

import network.command.Command;
import network.message.Message;

/**
 * Содержит методы для работы с коллекциией объектов класса {@code HumanBeing}
 */
public interface Storage extends StorageManagement {

    Message apply(Command<String, Storage> command, boolean history);
}