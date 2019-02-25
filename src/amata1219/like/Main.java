package amata1219.like;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.command.CommandExecutor;
import amata1219.like.command.CommandExecutor.Args;
import amata1219.like.command.LikeCCommand;
import amata1219.like.command.LikeCommand;
import amata1219.like.command.LikeICommand;
import amata1219.like.command.LikeLCommand;
import amata1219.like.command.LikeSCommand;

public class Main extends JavaPlugin implements Listener {

	private static Main plugin;

	private HashMap<String, CommandExecutor> commands;

	@Override
	public void onEnable(){
		plugin = this;

		Util.init();

		commands = new HashMap<>();
		commands.put("like", new LikeCommand());
		commands.put("likec", new LikeCCommand());
		commands.put("likel", new LikeLCommand());
		commands.put("likei", new LikeICommand());
		commands.put("likes", new LikeSCommand());

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
		if(e.isCancelled())
			return;

		if(!(e.getWhoClicked() instanceof Player))
			return;

		Inventory inventory = e.getClickedInventory();
		if(inventory == null)
			return;

		String title = inventory.getTitle();
		if(title == null)
			return;

		Player player = Util.castPlayer(e.getWhoClicked());
		String[] prefix = title.split("@");
		int slot = e.getSlot();
		switch(prefix[0]){
		case "§8Info":
			e.setCancelled(true);
			if(slot != 7)
				return;

			player.closeInventory();
			Util.tell(player, ChatColor.GREEN, "このLikeのお気に入りを解除しました。");
			Util.unfavorite(player, Util.Likes.get(Long.parseLong(prefix[1])));
			break;
		case "§8Admin":
		case "§8Edit":
			e.setCancelled(true);
			if(slot == 6){
				Util.edit.put(player.getUniqueId(), Util.Likes.get(Long.parseLong(prefix[1])));
				player.closeInventory();
				Util.tell(player, ChatColor.GREEN, "新しい表示内容をチャット欄に入力して下さい。");
			}else if(slot == 7){
				player.openInventory(Util.createConfirmMenu(Util.Likes.get(Long.parseLong(prefix[1]))));
			}
			break;
		case "§8Remove":
			e.setCancelled(true);
			if(slot != 4)
				return;

			Util.delete(Util.Likes.get(Long.parseLong(prefix[1])));
			player.closeInventory();
			Util.tell(player, ChatColor.GREEN, "Likeを削除しました。");
			break;
		case "§8Mine":

			break;
		case "§8MyLike":

		default:
			return;
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(e.isCancelled())
			return;

		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if(!Util.edit.containsKey(uuid))
			return;

		Util.changeLore(Util.edit.get(uuid), Util.color(e.getMessage()));
		Util.edit.remove(uuid);
		Util.tell(player, ChatColor.GREEN, "Likeの表示内容を更新しました。");
	}

}
