package client.handle;

import client.Authentication;
import client.Client;
import client.gui.App;
import client.gui.controllers.AuthenticationSceneController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import network.message.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor extends Task<Void> {
    public static final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void handle(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void call() {
        try {
            while (true) {
                Message message = queue.take();
                String answer = message.content;

                System.out.println(answer);

                Platform.runLater(() -> {
                    if (message instanceof CollectionMessage) {
                        Client.log.info("it's collection message");
                        if (!((CollectionMessage) message).tellEveryone)
                            App.onAuthenticationComplete();
                        App.collection.clear();
                        App.collection.addAll(((CollectionMessage) message).humanBeings);

                    } else if (message instanceof UpdateMessage) {
                        Client.log.info("update message");
                        UpdateMessage msg = (UpdateMessage) message;
                        App.collection.replace(msg.lastId, msg.newHumanBeing);

                    } else if (message instanceof DeleteMessage) {
                        Client.log.info("delete message");
                        App.collection.removeAll(((DeleteMessage) message).id);

                    } else if (message instanceof AddMessage) {
                        Client.log.info("instance of AddMessage");
                        App.collection.addAll(((AddMessage) message).humanBeings);

                    } else if (answer.startsWith("code")) {
                        Client.log.info("It starts with code");
                        App.onAuthenticationNotComplete(Authentication.checkReturnedCode(answer.charAt(4)));

                    } else if (message instanceof EditMessage) {
                        Client.log.info("edit message");
                        EditMessage msg = (EditMessage) message;
                        App.collection.replace(msg.humanBeing.getId(), msg.humanBeing);
                    }
                });
            }
        } catch (InterruptedException e) {
            Client.log.warning(e::toString);
        }

        return null;
    }
}
