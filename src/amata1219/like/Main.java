package amata1219.like;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.command.CommandExecutor;
import amata1219.like.command.CommandExecutor.Args;
import amata1219.like.command.LikeCCommand;
import amata1219.like.command.LikeCommand;
import amata1219.like.command.LikeLCommand;
import amata1219.like.command.LikeOpCommand;
import amata1219.like.command.LikeSCommand;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

	private static Main plugin;
	private static Economy economy;

	private HashMap<String, CommandExecutor> commands;

	@Override
	public void onEnable(){
		plugin = this;

		Plugin pl = getServer().getPluginManager().getPlugin("Vault");
		if(!(pl instanceof Vault))
			new NullPointerException("Not find Vault.");

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			new NullPointerException("Not find Vault.");

		economy = rsp.getProvider();

		Util.init();

		commands = new HashMap<>();
		commands.put("like", new LikeCommand());
		commands.put("likec", new LikeCCommand());
		commands.put("likel", new LikeLCommand());
		commands.put("likes", new LikeSCommand());
		commands.put("likeop", new LikeOpCommand());

		getServer().getOnlinePlayers().parallelStream()
		.map(Player::getUniqueId)
		.forEach(Util::loadPlayerData);

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable(){
		HandlerList.unregisterAll((JavaPlugin) this);

		List<UUID> list = getServer().getOnlinePlayers().parallelStream().map(Player::getUniqueId).collect(Collectors.toList());
		list.forEach(uuid -> Util.savePlayerData(uuid, false));

		Util.PlayerConfig.update();

		list.forEach(Util::unloadPlayerData);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		commands.get(command.getName()).onCommand(sender, new Args(args));
		return true;
	}

	public static Main getPlugin(){
		return plugin;
	}

	public static Economy getEconomy(){
		return economy;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Util.loadPlayerData(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		Util.savePlayerData(uuid, true);
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
			e.setCancelled(true);
			if(slot == 45){
				LikeInvs invs = Util.LikeInvs.get(player.getUniqueId());
				int page = Integer.parseInt(prefix[1]);
				if(!invs.hasBeforeMine(page))
					return;

				player.openInventory(invs.getBeforeMine(page));
			}else if(slot == 53){
				LikeInvs invs = Util.LikeInvs.get(player.getUniqueId());
				int page = Integer.parseInt(prefix[1]);
				if(!invs.hasNextMine(page))
					return;

				player.openInventory(invs.getNextMine(page));
			}else{
				touchIcon(player, e);
			}
			break;
		case "§8MyLike":
			e.setCancelled(true);
			if(slot == 45){
				LikeInvs invs = Util.LikeInvs.get(player.getUniqueId());
				int page = Integer.parseInt(prefix[1]);
				if(!invs.hasBeforeLike(page))
					return;

				player.openInventory(invs.getBeforeLike(page));
			}else if(slot == 53){
				LikeInvs invs = Util.LikeInvs.get(player.getUniqueId());
				int page = Integer.parseInt(prefix[1]);
				if(!invs.hasNextLike(page))
					return;

				player.openInventory(invs.getNextLike(page));
			}else{
				touchIcon(player, e);
			}
		default:
			return;
		}
	}

	private void touchIcon(Player player, InventoryClickEvent e){
		Like like = LikeInvs.toLike(e.getCurrentItem());
		if(like == null)
			return;

		Economy economy = Main.getEconomy();
		if(e.isLeftClick()){
			if(!economy.has(player, Util.Tp)){
				Util.tell(player, ChatColor.RED, "所持金が足りません。");
				return;
			}

			player.closeInventory();
			economy.withdrawPlayer(player, Util.Tp);
			player.teleport(like.getLocation(player.getLocation()));
		}else if(e.isRightClick()){
			if(!economy.has(player, Util.Invite)){
				Util.tell(player, ChatColor.RED, "所持金が足りません。");
				return;
			}

			player.closeInventory();
			economy.withdrawPlayer(player, Util.Invite);
			String message = Util.InviteMessage.replace(Util.PLACE_HOLDER_OF_PLAYER_NAME, player.getName())
			.replace(Util.PLACE_HOLDER_OF_LIKE_TEXT, like.getLore().getText());
			player.getNearbyEntities(Util.Range, Util.Range, Util.Range).parallelStream()
			.filter(Player.class::isInstance)
			.map(Player.class::cast)
			.forEach(user -> user.spigot().sendMessage(Util.createInviteButton(message.replace(Util.PLACE_HOLDER_OF_INVITE_USER, user.getName()), like)));
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
