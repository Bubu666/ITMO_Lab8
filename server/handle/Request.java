package server.handle;

import network.User;
import network.message.Message;
import server.ServerUser;

import java.net.Socket;
import java.util.Objects;

public class Request {
    public ServerUser user;
    public Message message;

    public Request(ServerUser user, Message message) {
        this.user = user;
        this.message = message;
    }

    public Socket socket() {
        return user.socket;
    }

    public Request set(Message message) {
        this.message = message;
        return this;
    }

    public Request set(User user) {
        this.user.updateAccount(user);
        return this;
    }

    public User user() {
        return user.user();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return socket().equals(request.socket());
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket());
    }
}