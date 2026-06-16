package ru.junior.rx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Schedulers {
    public static Scheduler io() {
        return new IOThreadScheduler();
    }

    public static Scheduler computation() {
        return new ComputationScheduler();
    }

    public static Scheduler single() {
        return new SingleThreadScheduler();
    }

    public static class IOThreadScheduler implements Scheduler {
        private final ExecutorService service = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        public void execute(Runnable task) {
            service.execute(task);
        }
    }

    public static class ComputationScheduler implements Scheduler {
        private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        public void execute(Runnable task) {
            service.execute(task);
        }
    }

    public static class SingleThreadScheduler implements Scheduler {
        private final ExecutorService service = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        public void execute(Runnable task) {
            service.execute(task);
        }
    }
}
