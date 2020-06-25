package client;

import client.command.ClientCommandWorker;
import client.gui.App;
import client.gui.inter.StringResource;
import network.Encryption;
import network.User;
import network.message.AuthorizationMessage;

import java.util.Scanner;

public class Authentication {
    private final static Scanner in = new Scanner(System.in);
    public static String inputString = null;
    public static boolean started = false;

    public static boolean isAuthorized;

    public static AuthorizationMessage authenticate() {
        started = true;
        System.out.print(
                "\r  Для того, чтобы пользоваться приложением, необходимо авторизоваться\n" +
                        "  Введите цифру выбранного действия:\n" +
                        "  1: Авторизация\n" +
                        "  2: Регистрация\n> "
        );

        Client.consoleLock.lock();

        try {
            while (true) {
                String input = "";

                if (inputString == null) {
                    System.out.print("\r> ");
                    input = in.nextLine();
                } else {
                    input = inputString;
                    inputString = null;
                }

                switch (input) {
                    case "1":
                        return LogIn();
                    case "2":
                        return register();
                    case "exit":
                    case "quit":
                        App.quit();
                    default:
                        break;
                }

                System.out.println("  Ошибка ввода");
            }

        } finally {
            started = false;
            inputString = null;
            Client.consoleLock.unlock();
        }

    }

    public static AuthorizationMessage register() {
        System.out.println("  Регистрация нового пользователя.\n" +
                "  Введите данные:");
        User user = readUserInfo();
        return new AuthorizationMessage(user.login(), user.password(), true);
    }

    public static AuthorizationMessage LogIn() {
        System.out.println("  Введите данные учетной записи:");
        User user = readUserInfo();
        return new AuthorizationMessage(user.login(), user.password(), false);
    }

    public static User readUserInfo() {
        String login;
        String password;

        //while ((login = Client.console.getLogin()).equals("")) ;
        //while ((password = Client.console.getPassword()).equals("")) ;

        //Client.account = new User(login, Encryption.SHA_384(password));
        return Client.account;
    }

    public static String checkReturnedCode(char code) {
        String result = "";
        switch (code) {
            case '3':
                result = StringResource.getBundle().getString("c_warn_userAlreadyRegistered");
                break;

            case '1':
                result = StringResource.getBundle().getString("c_warn_incorrectPassword");
                break;

            case '4':
                System.out.println("\r  Пользователь не авторизован");
                break;
        }
        isAuthorized = false;
        return result;
    }
}