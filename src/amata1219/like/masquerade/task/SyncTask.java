package amata1219.like.masquerade.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import amata1219.like.Main;

public interface SyncTask extends Runnable {

	public static SyncTask define(SyncTask sync){
		return sync;
	}

	public default void execute(){
		Bukkit.getScheduler().runTask(Main.plugin(), this);
	}

	public default void executeLater(long delay){
		Bukkit.getScheduler().runTaskLater(Main.plugin(), this, delay);
	}

	public default BukkitTask executeTimer(long period, long delay){
		return Bukkit.getScheduler().runTaskTimer(Main.plugin(), this, period, delay);
	}

	public default BukkitTask executeTimer(long interval){
		return executeTimer(interval, interval);
	}
	
}
