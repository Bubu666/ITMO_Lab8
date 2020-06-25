package server;

import network.User;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;

public class ServerUser {
    private User user;
    public final Socket socket;

    public ServerUser(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    public User user() {
        return user;
    }

    public String login() {
        return user.login();
    }

    public void updateAccount(User newUser) {
        user = newUser;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerUser))
            return false;
        return user.equals(((ServerUser) o).user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }

    @Override
    public String toString() {
        return user.login();
    }


}
