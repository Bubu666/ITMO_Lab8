package server.database;

import network.User;
import network.human.HumanBeing;
import server.Server;

import java.io.PrintWriter;
import java.sql.*;
import java.util.LinkedList;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DataBase {
    private static final String URL = "jdbc:postgresql://host:5432/name";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private static DataBase dataBase;

    private static Connection connection;

    static {
        try {
            dataBase = new DataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataBase() throws SQLException, JSchException {
        DriverManager.setLogWriter(new PrintWriter(System.out));

        /*JSch jsch = new JSch();
        Session session = jsch.getSession(USER, "host", 22);
        session.setPassword(PASSWORD);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        session.setPortForwardingL(1337, "host", 5432);*/


        //Class.forName("org.postgresql.Driver");/
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void interrupt() throws SQLException {
        connection.close();
    }

    public static DataBase getInstance() {
        return dataBase;
    }

    public static void main(String[] args) {
        getInstance().getAllPeople().forEach(System.out::println);
    }

    public LinkedList<HumanBeing> getAllPeople() {
        LinkedList<HumanBeing> collection = new LinkedList<>();

        try (Statement statement = connection.createStatement()) {
            String sql = "select * from collection";

            try (ResultSet rs = statement.executeQuery(sql)) {

                while (rs.next()) {
                    collection.add(
                            HumanBeing.rebuild(
                                    rs.getString(1), rs.getInt(2), rs.getString(3),
                                    rs.getInt(4), rs.getDouble(5), rs.getBoolean(6),
                                    rs.getBoolean(7), rs.getInt(8), rs.getString(9),
                                    rs.getInt(10), rs.getString(11), rs.getBoolean(12),
                                    rs.getTimestamp(13)
                            )
                    );
                }
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return collection;
    }

    public HumanBeing insertHuman(User user, HumanBeing human) {
        String sql = null;

        try (Statement statement = connection.createStatement()) {
            sql = "select nextval ('users_objects_id_seq');";

            try (ResultSet rs = statement.executeQuery(sql)) {
                rs.next();
                int id = rs.getInt(1);

                human.init(user.login(), id);

                sql = "insert into collection " + human.dbProperties() + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    if (human.initStatement(preparedStatement).executeUpdate() == 1)
                        return human;
                }
            }

        } catch (SQLException throwable) {
            Server.log.warning(sql);
            throwable.printStackTrace();
        }
        return null;
    }

    public boolean deleteHuman(User user, HumanBeing human) {
        try (Statement statement = connection.createStatement()) {

            String sql = "delete from collection where id = " + human.getId() + ";";

            return statement.executeUpdate(sql) == 1;

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    public boolean replaceById(HumanBeing h) {
        try {
            String sql = "update collection set name = ?, x = ?, y = ?, real_hero = ?, has_toothpick = ?, impact_speed = ?, soundtrack_name = ?, minutes_of_waiting = ?, mood = ?, cool_car = ? where id = " + h.getId() + ";";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, h.getName());
                statement.setInt(2, h.getCoordinates().getX());
                statement.setDouble(3, h.getCoordinates().getY());
                statement.setBoolean(4, h.isRealHero());
                statement.setBoolean(5, h.hasToothpick());
                statement.setInt(6, (int) h.getImpactSpeed());
                statement.setString(7, h.getSoundtrackName());
                statement.setInt(8, h.getMinutesOfWaiting());
                statement.setString(9, h.getMood().toString());
                statement.setBoolean(10, h.getCar().isCool());

                return statement.executeUpdate() == 1;
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean checkUser(User user) {
        final String sql = "select password from users where login = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String login = user.login();

            statement.setString(1, login);

            String password = user.password();

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next() && rs.getString("password").equals(password)) {
                    return true;
                }
            }

        } catch (SQLException th) {
            th.printStackTrace();
        }
        return false;
    }

    public boolean isThere(User user) {
        final String sql = "select * from users where login = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String login = user.login();

            statement.setString(1, login);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return true;
            }

        } catch (SQLException th) {
            th.printStackTrace();
        }
        return false;
    }

    public boolean addUser(User user) {
        final String sql = "insert into users (login, password) values (?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!isThere(user)) {
                statement.setString(1, user.login());
                statement.setString(2, user.password());
                return statement.executeUpdate() == 1;
            }

        } catch (SQLException th) {
            th.printStackTrace();
        }
        return false;
    }
}
