package server.handle;

import server.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Handler implements Runnable {
    protected BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
    protected static boolean interrupted = false;

    protected static final ForkJoinPool mainPool = ForkJoinPool.commonPool();

    public Handler(boolean executeInMainPool) {
        if (executeInMainPool)
            mainPool.execute(this);
        else
            new Thread(this).start();
    }

    public void add(Request request) {
        try {
            Server.log.info(() -> "add request");
            requestQueue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void interrupt() {
        interrupted = true;
        mainPool.shutdown();
    }
}