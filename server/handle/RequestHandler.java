package server.handle;

import network.User;
import network.command.Command;
import network.message.*;
import server.BlockingStorage;
import server.Server;
import server.database.Users;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RequestHandler extends Handler {
    private static final BlockingStorage storage = BlockingStorage.getInstance();

    public RequestHandler() {
        super(true);
    }

    @Override
    public void run() {
        try {
            while (!interrupted) {
                Request request = requestQueue.take();
                new RequestTask(request).fork();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class RequestTask extends RecursiveAction {
        private final Request request;

        RequestTask(Request request) {
            this.request = request;
        }

        @Override
        protected void compute() {

            Message msg = request.message;

            if (msg instanceof CommandMessage) {
                Server.log.info(() -> "command");

                storage.lock.lock();

                Command cmd = ((CommandMessage) msg).command;
                Message response = storage.apply(cmd, true);
                storage.lock.unlock();
                Server.online.addCommand(request.user(), cmd);

                if (response instanceof ChangeCollectionMessage
                        || (response instanceof CollectionMessage && ((CollectionMessage) response).tellEveryone)) {
                    Server.online.tellEveryone(response);
                    Server.inputHandler.add(request);
                    return;
                }

                request.set(response);

            } else if (msg instanceof AuthorizationMessage) {
                AuthorizationMessage message = (((AuthorizationMessage) msg));

                Server.log.info(() -> "authorization");
                User user = message.user;

                if (message.registration) {
                    if (Users.registerUser(user)) {
                        Server.log.info(() -> "code 0");
                        request.set(user).set(new CollectionMessage("code0", storage.getHumanBeings(), false));

                    } else {
                        Server.log.info(() -> "code 1");
                        request.set(Users.unknown()).set(new Message("code3"));
                    }

                } else {
                    if (Users.authenticate(((AuthorizationMessage) msg).user)) {
                        Server.log.info(() -> "code 0");
                        request.set(user).set(new CollectionMessage("code0", storage.getHumanBeings(),false));

                    } else {
                        Server.log.info(() -> "code 1");
                        request.set(Users.unknown()).set(new Message("code1"));
                    }
                }
            }

            Server.log.info("Request handler adds request to send");
            Server.outputHandler.add(request);


        }
    }
    static class Task extends RecursiveAction {
        @Override
        protected void compute() {

        }
    }
}