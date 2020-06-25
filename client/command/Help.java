package client.command;

import network.User;

public class Help extends ClientCommand {

    public static final String helpInfo = "  help : вывести информацию по клиентским командам\n  server_help : вывести информацию по серверным командам\n";

    public Help(User user) {
        super("help");
    }

    @Override
    public Integer execute(Integer input) {
        ClientCommandWorker.help();
        return input;
    }
}
