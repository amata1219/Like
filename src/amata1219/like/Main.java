package amata1219.like;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTouchableLine;

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
			new NullPointerException("Not found Vault.");

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			new NullPointerException("Not found Vault.");

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

		Util.unload();
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

		Inventory inventory = e.getInventory();
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
			.replace(Util.PLACE_HOLDER_OF_LIKE_TEXT, like.getLore());
			player.spigot().sendMessage(Util.createInviteButton(message.replace(Util.PLACE_HOLDER_OF_INVITE_USER, player.getName()), like));
			for(Entity entity : player.getNearbyEntities(Util.Range, Util.Range, Util.Range)){
				if(entity.getType() != EntityType.PLAYER)
					continue;

				Player receiver = (Player) entity;
				receiver.spigot().sendMessage(Util.createInviteButton(message.replace(Util.PLACE_HOLDER_OF_INVITE_USER, receiver.getName()), like));
			}
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

		e.setCancelled(true);

		Bukkit.getScheduler().runTask(this, () -> {
			Util.changeLore(Util.edit.get(uuid), Util.color(e.getMessage()));
			Util.edit.remove(uuid);
			Util.tell(player, ChatColor.GREEN, "Likeの表示内容を更新しました。");
		});
	}

	public static final Method setTouchHandler;
	static{
		Method arg1 = null;
		try {
			Class<?> CraftTouchableLine = CraftTouchableLine.class;
			arg1 = CraftTouchableLine.getDeclaredMethod("setTouchHandler", TouchHandler.class, World.class, double.class, double.class, double.class);
			arg1.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		setTouchHandler = arg1;
	}

	public static void applyTouchHandler(Like like, boolean delete){
		NamedHologram hologram = like.getHologram();
		Location loc = hologram.getLocation();
		loc.setPitch(90.0F);
		CraftTouchableLine line = (CraftTouchableLine) hologram.getLine(0);
		try {
			setTouchHandler.invoke(line, delete ? null : createTouchHandler(like), loc.getWorld(), loc.getX(), loc.getY() - line.getHeight() * 3, loc.getZ());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static TouchHandler createTouchHandler(Like like){
		return new TouchHandler(){

			@Override
			public void onTouch(Player player) {
				UUID uuid = player.getUniqueId();
				if(player.isSneaking()){
					if(like.isOwner(uuid))
						player.openInventory(Util.createEditMenu(like));
					else
						player.openInventory(player.hasPermission(Util.OP_PERMISSION) ? Util.createAdminMenu(like) : Util.createInfoMenu(like));
				}else{
					if(like.isOwner(uuid)){
						Util.tell(player, ChatColor.RED, "自分のLikeはお気に入りに登録出来ません。");
						return;
					}

					if(Util.MyLikes.get(uuid).isRegisteredLike(like)){
						Util.tell(player, ChatColor.RED, "このLikeは既にお気に入りに登録しています。");
						return;
					}

					Util.favorite(player, like);
					Util.tell(player, ChatColor.GREEN, "このLikeをお気に入りに登録しました。");
					player.sendMessage(Util.Tip);
				}
			}

		};
	}

}
