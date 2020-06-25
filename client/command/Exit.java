package client.command;

import client.gui.App;
import network.User;

import java.lang.reflect.InvocationTargetException;

public class Exit extends ClientCommand {

    public static final String helpInfo = "  exit/quit : завершение работы\n";

    public Exit(User user) {
        super("exit");
    }

    @Override
    public Integer execute(Integer object) {
        App.quit();
        return object;
    }
}
