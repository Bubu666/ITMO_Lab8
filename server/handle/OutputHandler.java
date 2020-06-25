package server.handle;

import network.message.Message;
import server.Server;
import server.ServerUser;
import server.io.ServerMessageSender;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.logging.SocketHandler;

public class OutputHandler extends Handler {
    public OutputHandler() {
        super(false);
    }

    public void tellEveryOne(Map<Socket, ServerUser> users, Message message) {
        users.forEach((sock, user) -> {
            Server.log.info("sending a message to user " + user.login());
            try {
                ServerMessageSender sender = new ServerMessageSender(sock.getOutputStream(), user.login());
                sender.sendMessage(message);
            } catch (IOException e) {
                Server.log.warning(e::getLocalizedMessage);
                Server.log.info("disconnected by OutputHandler");
                Server.online.disconnect(user);
            }
        });
    }

    @Override
    public void run() {
        try {
            while (!interrupted) {
                Request request = requestQueue.take();

                try {
                    ServerMessageSender sender
                            = new ServerMessageSender(request.socket().getOutputStream(), request.user.login());

                    Server.log.info(() -> "sending a message to user " + request.user.login());

                    sender.sendMessage(request.message);

                    Server.inputHandler.add(request);

                } catch (Exception e) {
                    Server.log.warning(e::getLocalizedMessage);
                    Server.log.info("disconnected by OutputHandler");
                    Server.online.disconnect(request.user);
                }

            }
        } catch (InterruptedException e) {
            Server.log.warning(e::toString);
        }
    }
}
