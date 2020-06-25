package client;

import client.animation.WaitingForServerAnimation;
import client.gui.App;
import client.handle.InputHandler;
import client.handle.OutputHandler;
import client.handle.Processor;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import network.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

//import client.handle.ConsoleHandler;

public class Client {
    public static final ForkJoinPool mainPool = ForkJoinPool.commonPool();
    public static SocketChannel channel;
    private static int serverPort = 13337;

    private static WaitingForServerAnimation waitingAnimation = new WaitingForServerAnimation();

    public static final ReentrantLock consoleLock = new ReentrantLock();

    //public static ConsoleHandler console = new ConsoleHandler();

    public static User account;

    public static final OutputHandler out = new OutputHandler();
    public static final Processor processor = new Processor();
    public static final InputHandler in = new InputHandler();

    public static Service<Void> processorService;

    public static Selector selector;

    public static final Logger log = Logger.getLogger(Client.class.getName());

    public static void start() {

        Client.log.info("init");
        createSshTunnel();
        mainPool.execute(out);

        processorService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return processor;
            }
        };

        processorService.start();

        establishConnection();
        mainPool.invoke(in);
    }

    public static void createSshTunnel() {
        while (true) {
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession("s285691", "se.ifmo.ru", 2222);
                session.setPassword("rty686");
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                Random r = new Random();
                serverPort = r.nextInt(60000) + 2000;

                session.setPortForwardingL(serverPort, "localhost", 13337);

                return;
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }
    }

    public static void establishConnection() {
        mainPool.invoke(new Connection());
        selector.wakeup();
    }

    private static class Connection extends RecursiveAction {
        @Override
        protected void compute() {
            try {
                if (channel != null) {
                    channel.close();
                }

                if (selector != null) {
                    selector.close();
                }

                channel = SocketChannel.open();
                selector = Selector.open();
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);

                channel.connect(new InetSocketAddress("localhost", serverPort));
                channel.finishConnect();

                App.authenticationController.pn_authorization.setDisable(false);

            } catch (IOException e) {
                Client.log.warning(e.toString());
            }
        }
    }
}