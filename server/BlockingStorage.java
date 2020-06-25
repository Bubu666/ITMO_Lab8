package server;

import network.User;
import network.command.Command;
import network.command.HelpInfo;
import network.human.HumanBeing;
import network.message.*;
import network.storage.AbstractStorage;
import network.storage.Storage;
import network.storageCommands.*;
import server.database.DataBase;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class BlockingStorage extends AbstractStorage {
    private static final BlockingStorage instance;
    private static final DataBase dataBase = DataBase.getInstance();

    private static Path clientScriptsPath;

    public ReentrantLock lock = new ReentrantLock();

    static {
        instance = new BlockingStorage();

        try {
            clientScriptsPath = Paths.get(
                    Paths.get(Server.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI()).getParent() + "/clientScripts"
            );

            if (Files.notExists(clientScriptsPath))
                Files.createDirectories(clientScriptsPath);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static BlockingStorage getInstance() {
        return instance;
    }

    private BlockingStorage() {
        super(BlockingStorage.class);
        this.humanBeings = dataBase.getAllPeople();
    }

    @Override
    public Message apply(Command<String, Storage> command, boolean history) {

        try {
            if (history) commandList.addFirst(command);

            Object result = command.execute(this);

            if (result instanceof ChangeCollectionMessage || result instanceof CollectionMessage) {
                return (Message) result;
            } else {
                return new Message((String) result);
            }

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new Message("error");
    }

    @HelpInfo(info = Scripts.helpInfo)
    public String scripts(User user) {
        try {
            return Files.list(clientScriptsPath).map(Path::getFileName).map(f -> "  " + f).reduce((s1, s2) -> s1 + s2).orElse("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "  возникла ошибка";
    }

    @Override
    @HelpInfo(info = DeleteScript.helpInfo)
    public String delete_script(String name, User user) {
        try {
            if (Files.deleteIfExists(Paths.get(clientScriptsPath + "/" + name)))
                return "  скрипт " + name + " удален";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    @HelpInfo(info = DeleteAllScripts.helpInfo)
    public String delete_all_scripts(User user) {
        try {
            return Files.list(clientScriptsPath)
                    .map(f -> f.getFileName().toString())
                    .map(s -> delete_script(s, user)).reduce((s1, s2) -> s1 + s2).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "  возникла ошибка";
    }

    @Override
    @HelpInfo(info = Edit.helpInfo)
    public Message edit(User user, HumanBeing humanBeing) {
        dataBase.replaceById(humanBeing);
        humanBeings.remove(humanBeing);
        humanBeings.add(humanBeing);
        return new EditMessage("Элемент обновлен", humanBeing);
    }

    /**
     * Вывод в консоль информации о доступных командах
     */
    @Override
    @HelpInfo(info = ServerHelp.helpInfo)
    public String server_help(User user) {
        return "  Допустимые команды:\n" + helpInfo + "  * Некоторые команды поддерживают ввод случайно сгенерированного объекта (random)";
    }

    @Override
    @HelpInfo(info = AllInfo.helpInfo)
    public String all_info(User user) {
        return "  Тип коллекции: " + humanBeings.getClass().getName() + "\n  Дата инициализации: " + dateOfInitialization + "\n  Количество элементов: " + humanBeings.size();
    }

    /**
     * Вывод в консоль информации о коллекции {@code humanBeings}
     */
    @Override
    @HelpInfo(info = Info.helpInfo)
    public String info(User user) {
        String login = user.login();
        return "  Тип коллекции: " + humanBeings.getClass().getName() + "\n  Дата инициализации: " + dateOfInitialization + "\n  Количество элементов: " + humanBeings.stream().filter(h -> h.getOwner().equals(login)).count()
                + "\n  Пользователь: " + login;
    }

    /**
     * Вывод в консоль объектов коллекции {@code humanBeings}
     */
    @Override
    @HelpInfo(info = Show.helpInfo)
    public String show(User user) {
        String login = user.login();

        return humanBeings.stream().filter(h -> h.getOwner().equals(login)).map(HumanBeing::toString)
                .reduce((h1, h2) -> h1 + "\n" + h2).orElse("  Коллекция пуста");
    }

    @Override
    @HelpInfo(info = ShowAnother.helpInfo)
    public String show_another(User user, String another) {
        return humanBeings.stream().filter(h -> h.getOwner().equals(another)).map(HumanBeing::toString)
                .reduce((h1, h2) -> h1 + "\n" + h2).orElse("  Коллекция пуста");
    }

    @Override
    @HelpInfo(info = ShowAll.helpInfo)
    public String show_all(User user) {
        return humanBeings.stream().map(HumanBeing::toString)
                .reduce((h1, h2) -> h1 + "\n" + h2).orElse("  Коллекция пуста");
    }

    /**
     * Добавление объекта в коллекцию {@code humanBeings}
     *
     * @param beings Добавляемый объект
     */
    @Override
    @HelpInfo(info = Add.helpInfo)
    public Message add(HumanBeing[] beings, User user) {
        if (beings.length > 200)
            return new Message("  Нельзя добавить больше 200 объектов за раз");

        ArrayList<HumanBeing> result = new ArrayList<>();
        boolean success = true;

        for (int i = 0; i < beings.length; ++i) {
            if (dataBase.insertHuman(user, beings[i]) != null) {
                humanBeings.add(beings[i]);
                result.add(beings[i]);

            } else {
                success = false;
            }
        }

        if (!success)
            return new Message("Возникли ошибки при работе с базой данных, возможно, часть объектов потеряна");

        return new AddMessage("add", result.toArray(new HumanBeing[0]));
    }

    /**
     * Обновление объекта коллекции {@code humanBeings} по заданному id
     *
     * @param id         Id объекта
     * @param humanBeing Объект
     */
    @Override
    @HelpInfo(info = Update.helpInfo)
    public Message update(int id, HumanBeing humanBeing, User user) {

        if (humanBeings.size() == (humanBeings = humanBeings.stream().filter(hb -> {
            if (hb.getId() == id) {
                dataBase.deleteHuman(user, hb);
                return false;
            }
            return true;
        }).collect(Collectors.toCollection(LinkedList::new))).size()) {
            return new Message("В коллекции нет элемента с данным id");
        } else {
            if (dataBase.insertHuman(user, humanBeing) != null)
                humanBeings.add(humanBeing);
            return new UpdateMessage("Коллекция обновлена", id, humanBeing);
        }
    }

    /**
     * Удаление объекта коллекции {@code humanBeings} по заданному id
     *
     * @param id Id удаляемого объекта
     */
    @Override
    @HelpInfo(info = RemoveById.helpInfo)
    public Message remove_by_id(int id, User user) {
        return humanBeings.size() == (humanBeings = humanBeings.stream().filter(hb -> {

            if (hb.getId() == id) {
                dataBase.deleteHuman(user, hb);
                return false;
            }
            return true;

        }).collect(Collectors.toCollection(LinkedList::new))).size()
                ? new Message("Элемент не удален") : new DeleteMessage("Элемент удален", new int[]{id});
    }

    /**
     * очистка коллеции {@code humanBeings}
     */
    @Override
    @HelpInfo(info = Clear.helpInfo)
    public Message clear(User user) {
        String owner = user.login();
        HumanBeing[] people = humanBeings.stream().filter(hb -> hb.getOwner().equals(owner)).toArray(HumanBeing[]::new);


        if (people.length > 0) {
            for (HumanBeing human : people) {
                dataBase.deleteHuman(user, human);
                humanBeings.remove(human);
            }
            return new DeleteMessage("Все элементы вашей коллекции удалены",
                    Arrays.stream(people).mapToInt(HumanBeing::getId).toArray());
        } else {
            return new Message("Ваша коллекция пуста");
        }
    }

    /**
     * сохранение коллекции {@code humanBeings} в файл
     */
    @Override
    public String save() throws IOException {
        return null;
    }

    /**
     * Выполнение скрипта из файла по заданному имени
     *
     */
    @Override
    @HelpInfo(info = ExecuteScript.helpInfo)
    public Message execute_script(List<Command> script, User user) {
        Server.log.info("executing script");
        script.forEach(c -> this.apply(c, false));
        return new CollectionMessage("script", getHumanBeings(), true);
        /*Path scriptPath = Paths.get(clientScriptsPath + "/" + fileName);

        if (!Files.exists(scriptPath)) {
            return "  Скрипт " + fileName + " не существует.\n  Для создания скрипта используйте create_script";

        }

        LinkedList<Command> script = new LinkedList<>();

        File scriptFile = scriptPath.toFile();

        try (ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(scriptFile))) {

            while (true) {
                final Command command = (Command) objIn.readObject();

                if (command instanceof ExecuteScript) {
                    if (((ExecuteScript) command).getFileName().equals(fileName)) {
                        return "  Скрипт " + fileName + " является зацикленным и не может быть исполнен";
                    }
                }

                script.add(command);
            }

        } catch (ClassNotFoundException e) {
            return "  Скрипт " + fileName + " не корректен";
        } catch (IOException ignored) {
        }

        try {
            return "deprecated";//script.stream().map(c -> this.apply(c, false)).reduce((s1, s2) -> s1 + s2).get();
        } catch (StackOverflowError e) {
            return "  Скрипт " + fileName + " является зацикленным и не может быть исполнен";
        }*/
    }

    /**
     * Принудительное завершение программы
     */
    @Override
    public String exit(User user) {
        return null;
    }

    /**
     * Удаление из коллекции {@code humanBeings} последнего добавленного элемента
     */
    @Override
    @HelpInfo(info = RemoveHead.helpInfo)
    public Message remove_head(User user) {

        HumanBeing human = humanBeings.stream()
                .filter(hb -> hb.getOwner().equals(user.login())).findFirst().orElse(null);

        if (human == null) {
            return new Message("Ваша коллекция пуста");
        } else {
            dataBase.deleteHuman(user, human);
            humanBeings.remove(human);
            return new DeleteMessage("Элемент удален", new int[]{human.getId()});
        }
    }

    /**
     * Удаление объектов коллекции {@code humanBeings},
     * которые привышают заданный элемент
     *
     * @param delimiter Заданный элемент
     */
    @Override
    @HelpInfo(info = RemoveGreater.helpInfo)
    public Message remove_greater(HumanBeing delimiter, User user) {
        int prevSize = humanBeings.size();
        String owner = user.login();

        ArrayList<HumanBeing> result = new ArrayList<>();

        humanBeings = humanBeings.stream().filter(hb -> {
            if (hb.getOwner().equals(owner) && hb.getMinutesOfWaiting() > delimiter.getMinutesOfWaiting()) {
                dataBase.deleteHuman(user, hb);
                result.add(hb);
                return false;
            }
            return true;
        }).collect(Collectors.toCollection(LinkedList::new));

        return new DeleteMessage("Удалено " + (prevSize - humanBeings.size()) + " элементов",
                result.stream().mapToInt(HumanBeing::getId).toArray());
    }

    /**
     * Вывод в консоль последних пяти команд {@code commandList}
     */
    @Override
    @HelpInfo(info = History.helpInfo)
    public String history(User user) {
        LinkedList<Command> history = Server.online.getHistoryFor(user);
        return history.stream().map(Command::toString).reduce((s1, s2) -> s1 + "\n" + s2).get();
    }

    /**
     * Вывод в консоль объекта коллекции {@code humanBeings}, который имеет максимальное значение переменной {@code mood}
     */
    @Override
    @HelpInfo(info = MaxByMood.helpInfo)
    public String max_by_mood(User user) {
        HumanBeing result = humanBeings.stream()
                .max(Comparator.comparing(HumanBeing::getMood)).orElse(null);

        if (result == null)
            return "коллекция пуста";

        return result.toString();
    }

    /**
     * Вывод в консоль количества объектов коллекции {@code humanBeings}
     * с заданным значением поля {@code soundtrackName}
     *
     * @param soundtrackName Заданное значение поля {@code soundtrackName}
     */
    @Override
    @HelpInfo(info = CountBySoundtrackName.helpInfo)
    public String count_by_soundtrack_name(String soundtrackName, User user) {
        return "" + humanBeings.stream()
                .filter(h -> h.getSoundtrackName().equals(soundtrackName)).count();
    }

    /**
     * Вывод в консоль значений поля {@code soundtrackName} объектов коллекции {@code humanBeings} в порядке убывания
     */
    @Override
    @HelpInfo(info = PrintFieldDescendingSoundtrackName.helpInfo)
    public String print_field_descending_soundtrack_name(User user) {
        StringBuilder strBldr = new StringBuilder();

        humanBeings.stream().map(HumanBeing::getSoundtrackName).sorted(Comparator.reverseOrder())
                .forEachOrdered(s -> strBldr.append(s).append("\n"));

        String result = strBldr.toString();
        return result.equals("") ? "Коллекция пуста!" : result;
    }

    @Override
    @HelpInfo(info = CreateScript.helpInfo)
    public String create_script(List<Command> script, String scriptName, User user) {
        try {
            Path scriptFilePath = Paths.get(clientScriptsPath + "/" + scriptName);
            if (!Files.exists(scriptFilePath)) Files.createFile(scriptFilePath);

            File scriptFile = scriptFilePath.toFile();

            try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(scriptFile))) {
                for (Command command : script) {
                    objOut.writeObject(command);
                }
            }

            return "Скрипт создан";
        } catch (IOException e) {
            return "Скрипт не создан: " + e.getMessage();
        }
    }

    public HumanBeing[] getHumanBeings() {
        HumanBeing[] humans = humanBeings.toArray(new HumanBeing[0]);
        Arrays.sort(humans);
        return humans;
    }
}
