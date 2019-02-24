package amata1219.like;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.command.CommandExecutor;
import amata1219.like.command.CommandExecutor.Args;
import amata1219.like.command.LikeCommand;

public class Main extends JavaPlugin implements Listener {

	private static Main plugin;

	private HashMap<String, CommandExecutor> commands;

	@Override
	public void onEnable(){
		plugin = this;

		Util.init();

		commands = new HashMap<>();
		commands.put("like", new LikeCommand());

		getServer().getOnlinePlayers().parallelStream()
		.map(Player::getUniqueId)
		.forEach(Util::loadPlayerData);

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable(){
		HandlerList.unregisterAll((JavaPlugin) this);

		Stream<UUID> stream = getServer().getOnlinePlayers().parallelStream()
		.map(Player::getUniqueId);

		stream.forEach(uuid -> Util.savePlayerData(uuid, false, true));

		Util.PlayerConfig.update();

		stream.forEach(Util::unloadPlayerData);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		commands.get(command.getName()).onCommand(sender, new Args(args));
		return true;
	}

	public static Main getPlugin(){
		return plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Util.loadPlayerData(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		Util.savePlayerData(uuid, true, false);
		Util.unloadPlayerData(uuid);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e){

	}

}
