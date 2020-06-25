package network.storageCommands;

import network.User;
import network.command.Command;
import network.message.Message;
import network.storage.Storage;

import java.util.List;

/**
 * Команда "execute_script"
 */
public class ExecuteScript extends StorageCommand<Message> {

    private final List<Command> script;
    private final User user;
    /**
     * Информация о использовании команды
     */
    public final static String helpInfo = "  execute_script file_name : считать и исполнить скрипт из указанного файла. \n";

    /**
     * Принимает имя файла
     * @param user
     */
    public ExecuteScript(List<Command> script, User user) {
        super("execute_script");
        this.script = script;
        this.user = user;
    }

    /**
     * Реализация команды
     */
    @Override
    public Message execute(Storage storage) {
        return storage.execute_script(script, user);
    }
}
