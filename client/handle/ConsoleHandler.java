package client.handle;

import client.Client;
import client.ConsoleParser;
import client.command.ClientCommand;
import network.command.Command;
import network.message.CommandMessage;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.RecursiveAction;

public class ConsoleHandler extends RecursiveAction {
    private static final Console console = System.console();
    public static String inputString;
    public static boolean waitingForAnswer;
    private static final Scanner consoleScanner = new Scanner(System.in);

    private final ConsoleParser parser = new ConsoleParser();

    public String getPassword() {
        if (console == null) {
            System.out.print("Password: ");
            return consoleScanner.nextLine();
        }
        char[] chars = console.readPassword("%s", "Password: ");
        return new String(chars, 0, chars.length);
    }

    public String getLogin() {
        if (console == null) {
            System.out.print("Login: ");
            return consoleScanner.nextLine();
        }
        return console.readLine("%s", "Login: ");
    }

    public boolean askQuit() {
        waitingForAnswer = true;

        Client.consoleLock.lock();

        System.out.println("\r  Не удалось установить соединение с сервером\n  Завершить работу приложения? (y/n)");

        try {
            String answer;

            while (true) {
                if (inputString != null) {
                    answer = inputString;
                    inputString = null;
                } else {
                    if (console == null) {
                        System.out.print("> ");
                        answer = consoleScanner.nextLine();
                    } else {
                        answer = console.readLine("%s", "> ");
                    }

                }

                if (answer.startsWith("n") || answer.startsWith("N"))
                    return false;
                if (answer.startsWith("y") || answer.startsWith("Y"))
                    return true;
                System.out.println("  (y/n)");
            }
        } finally {
            inputString = null;
            waitingForAnswer = false;
            Client.consoleLock.unlock();
        }
    }

    @Override
    protected void compute() {
        try {
            while (true) {
                Command cmd;

                while (true) {
                    Client.consoleLock.lock();

                    try {
                        System.out.print("\r> ");
                        cmd = parser.getCommand(System.in);

                        if (cmd != null) break;
                    } finally {
                        Client.consoleLock.unlock();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (cmd instanceof ClientCommand) {
                    cmd.execute(0);

                } else {
                    Client.out.send(new CommandMessage("command", cmd));
                }

            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
