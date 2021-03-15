package amata1219.like;

import org.bukkit.Bukkit;

import java.util.function.BiConsumer;

public abstract class ChainedTask implements Runnable {

    private static final BiConsumer<ChainedTask, Long> runTaskSynchronously = (runner, delay) -> Bukkit.getScheduler().runTaskLater(Main.plugin(), runner, delay);
    private static final BiConsumer<ChainedTask, Long> runTaskAsynchronously = (runner, delay) -> Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin(), runner, delay);

    private final BiConsumer<ChainedTask, Long> runTask;
    private ChainedTask head;
    private ChainedTask next;
    private long delay;

    private ChainedTask(BiConsumer<ChainedTask, Long> runTask) {
        this.runTask = runTask;
    }

    private static ChainedTask of(long delay, Runnable processing, BiConsumer<ChainedTask, Long> runTask) {
        ChainedTask runner = new ChainedTask(runTask) {
            @Override
            public void process() {
                processing.run();
            }
        };
        ChainedTask head = new ChainedTask(runTask) {
            @Override
            public void process() {

            }
        };
        head.next = runner;
        head.delay = delay;
        runner.head = head;
        return runner;
    }

    public static ChainedTask synchronously(long delay, Runnable processing) {
        return of(delay, processing, runTaskSynchronously);
    }

    public static ChainedTask asynchronously(long delay, Runnable processing) {
        return of(delay, processing, runTaskAsynchronously);
    }

    @Override
    public void run() {
        process();
        if (next != null) runTask.accept(next, delay);
    }

    public abstract void process();

    public ChainedTask append(long delay, Runnable processing, BiConsumer<ChainedTask, Long> runTask) {
        ChainedTask next = new ChainedTask(runTask) {
            @Override
            public void process() {
                processing.run();
            }
        };
        next.head = head;
        this.next = next;
        this.delay = delay;
        return next;
    }

    public void runTaskLater() {
        head.run();
    }

}