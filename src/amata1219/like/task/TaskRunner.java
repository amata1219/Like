package amata1219.like.task;

import amata1219.like.Main;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class TaskRunner {

    private static JavaPlugin plugin() {
        return Main.plugin();
    }

    public static BukkitScheduler scheduler() {
        return plugin().getServer().getScheduler();
    }

    public static void runTaskSynchronously(Consumer<BukkitTask> processing) {
        scheduler().runTask(plugin(), processing);
    }

    public static void runTaskLaterSynchronously(Consumer<BukkitTask> processing, long delay) {
        scheduler().runTaskLater(plugin(), processing, delay);
    }

    public static void runTaskTimerSynchronously(Consumer<BukkitTask> processing, long delay, long period) {
        scheduler().runTaskTimer(plugin(), processing, delay, period);
    }

    public static void runTaskAsynchronously(Consumer<BukkitTask> processing) {
        scheduler().runTaskAsynchronously(plugin(), processing);
    }

    public static void runTaskLaterAsynchronously(Consumer<BukkitTask> processing, long delay) {
        scheduler().runTaskLaterAsynchronously(plugin(), processing, delay);
    }

    public static void runTaskTimerAsynchronously(Consumer<BukkitTask> processing, long delay, long period) {
        scheduler().runTaskTimerAsynchronously(plugin(), processing, delay, period);
    }

}
