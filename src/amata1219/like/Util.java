package amata1219.like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import amata1219.like.chunk.LikeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Util {

	public static Config Config;
	public static Config LikeConfig;
	public static Config PlayerConfig;
	public static Config LimitConfig;

	public static final String PLACE_HOLDER_OF_LIKE_COUNT = "%like_count%";
	public static final String PLACE_HOLDER_OF_PLAYER_NAME = "%player%";
	public static final String PLACE_HOLDER_OF_LIKE_TEXT = "%Like_text%";
	public static final String PLACE_HOLDER_OF_INVITE_USER = "%invete_user%";

	public static HashMap<String, String> Worlds = new HashMap<>();
	
	public static String Counter;
	public static String Lore;
	public static String Message;
	public static String Tip;
	
	public static int CooldownTime;
	public static int UpperLimit;
	
	public static Material LikeCount;
	public static Material Timestamp;
	public static Material Id;
	public static Material Edit;
	public static Material Remove1;
	public static Material Remove2;
	public static Material Unfavorite;
	public static Material OtherLike;
	public static Material PageButton;
	public static Material LikeIcon;
	
	public static double Tp;
	public static double Invite;
	public static int Range;
	public static String InviteMessage;

	public static HashMap<Long, OldLike> Likes = new HashMap<>();
	public static LikeMap LikeMap = new LikeMap();
	public static HashMap<UUID, List<OldLike>> Mines = new HashMap<>();

	public static HashMap<UUID, LikeMap> MyLikes = new HashMap<>();
	public static HashMap<UUID, LikeInvs> LikeInvs = new HashMap<>();

	public static HashMap<UUID, OldLike> edit = new HashMap<>();
	public static List<UUID> cooldown = new ArrayList<>();

	public static final String TOKEN = String.valueOf(System.nanoTime());

	public static final String OP_PERMISSION = "like.likeop";

	public static void init(){
		
		Config = new Config("config");
		LikeConfig = new Config("like_data");
		PlayerConfig = new Config("player_data");
		LimitConfig = new Config("like_limit");

		loadConfigValues();
		FileConfiguration config = LikeConfig.get();
		for(String name: config.getKeys(false)){
			NamedHologram hologram = NamedHologramManager.getHologram(name);
			if(hologram == null){
				System.out.println("Hologram is not found -> " + name);
				continue;
			}
			long id = Long.parseLong(name);
			String[] data = config.getString(name).split(",");
			OldLike like = new OldLike(hologram, UUID.fromString(data[0]), Integer.parseInt(data[1]));
			OldMain.applyTouchHandler(like, false);
			Likes.put(id, like);
			LikeMap.put(like);
			addMine(like);
		}

		for(Player player : OldMain.getPlugin().getServer().getOnlinePlayers())
			loadPlayerData(player.getUniqueId());
	}

	public static void unload(){
		for(OldLike like : Likes.values()){
			LikeConfig.get().set(like.getStringId(), like.toString());
			like.save(false);
		}
		LikeConfig.update();
		HologramDatabase.trySaveToDisk();
	}

	public static void loadConfigValues(){
		FileConfiguration config = Config.get();

		Worlds.clear();
		config.getStringList("Worlds").parallelStream()
		.map(s -> s.split(":"))
		.forEach(s -> Worlds.put(s[0], color(s[1])));

		ConfigurationSection lines = config.getConfigurationSection("TextLines");
		Counter = color(lines.getString("Counter"));
		Lore = color(lines.getString("Lore"));
		Message = color(lines.getString("Message"));

		Tip = color(config.getString("TIP"));
		CooldownTime = config.getInt("CooldownTime") * 20;
		UpperLimit = config.getInt("UpperLimit");

		ConfigurationSection items = config.getConfigurationSection("Items");
		LikeCount = type(items.getString("LikeCount"));
		Timestamp = type(items.getString("Timestamp"));
		Id = type(items.getString("Id"));
		Edit = type(items.getString("Edit"));
		Remove1 = type(items.getString("Remove1"));
		Remove2 = type(items.getString("Remove2"));
		Unfavorite = type(items.getString("Unfavorite"));
		OtherLike = type(items.getString("OtherLike"));
		PageButton = type(items.getString("PageButton"));
		LikeIcon = type(items.getString("LikeIcon"));

		Tp = config.getDouble("TPCost");

		ConfigurationSection invite = config.getConfigurationSection("Invite");
		Invite = invite.getDouble("Cost");
		Range = invite.getInt("Range");
		InviteMessage = color(invite.getString("Message"));
	}

	public static void loadPlayerData(UUID uuid){
		MyLikes.put(uuid, new LikeMap(uuid));
		LikeInvs.put(uuid, new LikeInvs(uuid));
		String s = uuid.toString();
		FileConfiguration c = LimitConfig.get();
		if(!c.contains(s)){
			c.set(s, c.getInt("Default"));
			LimitConfig.update();
		}
	}

	public static void savePlayerData(UUID uuid, boolean update){
		PlayerConfig.get().set(uuid.toString(), MyLikes.get(uuid).toString());
		if(update)
			PlayerConfig.update();
	}

	public static void unloadPlayerData(UUID uuid){
		MyLikes.remove(uuid);
		LikeInvs.remove(uuid);
	}

	public static void addMine(OldLike like){
		UUID owner = like.getOwner();
		List<OldLike> list = Mines.get(owner);
		if(list == null)
			Mines.put(owner, list = new ArrayList<>());
		list.add(like);
	}

	public static void removeMine(OldLike like){
		UUID owner = like.getOwner();
		List<OldLike> list = Mines.get(owner);
		if(list == null)
			return;

		list.remove(like);
		if(list.isEmpty())
			Mines.remove(owner);
	}

	public static String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	private static Material type(String s){
		return Material.valueOf(s);
	}

	public static void tell(CommandSender sender, ChatColor color, String message){
		sender.sendMessage(color + message);
	}

	public static boolean isNotPlayer(CommandSender sender){
		if(sender instanceof Player)
			return false;

		sender.sendMessage(ChatColor.RED + "ゲーム内から実行して下さい。");
		return true;
	}

	public static Player castPlayer(CommandSender sender){
		return (Player) sender;
	}

	public static String getName(UUID uuid){
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		String name = player.getName();
		return (player == null || name == null) ? "Unknown" : name;
	}

	public static Inventory createInventory(int size, String title){
		return Bukkit.createInventory(null, size, ChatColor.DARK_GRAY + title);
	}

	public static Inventory createInfoMenu(OldLike like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(18, "Info@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.GREEN + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(0, owner);
		inventory.setItem(3, newItem(LikeCount, "§aお気に入りの数:§f " + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "§a作成日時:§f " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "§a管理ID:§f " + like.getId()));
		inventory.setItem(6, newItem(Unfavorite, "§aお気に入りの解除"));
		inventory.setItem(9, newItem(OtherLike, "§aこの作者の他のLike情報"));

		setOtherLike(inventory, like);
		return inventory;
	}

	public static Inventory createEditMenu(OldLike like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(9, "Edit@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.GREEN + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(0, owner);
		inventory.setItem(3, newItem(LikeCount, "§aお気に入りの数:§f " + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "§a作成日時:§f " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "§a管理ID:§f " + like.getId()));
		inventory.setItem(7, newItem(Edit, "§a表示内容の編集"));
		inventory.setItem(8, newItem(Remove1, "§aLikeの削除"));
		return inventory;
	}

	public static Inventory createConfirmMenu(OldLike like){
		Inventory inventory = createInventory(9, "Remove@" + like.getStringId());
		inventory.setItem(4, newItem(Remove2, ChatColor.RED + "Likeを削除する(※元に戻せません)"));
		return inventory;
	}

	public static Inventory createAdminMenu(OldLike like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(18, "Admin@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.GREEN + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(0, owner);
		inventory.setItem(3, newItem(LikeCount, "§aお気に入りの数:§f " + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "§a作成日時:§f " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "§a管理ID:§f " + like.getId()));
		inventory.setItem(6, newItem(Unfavorite, "§aお気に入りの解除"));
		inventory.setItem(7, newItem(Edit, "§a表示内容の編集"));
		inventory.setItem(8, newItem(Remove1, "§aLikeの削除"));
		inventory.setItem(9, newItem(OtherLike, "§aこの作者の他のLike情報"));

		setOtherLike(inventory, like);
		return inventory;
	}

	public static void setOtherLike(Inventory inventory, OldLike like){
		UUID owner = like.getOwner();
		if(!Mines.containsKey(owner))
			return;

		List<OldLike> list = new ArrayList<>(Mines.get(like.getOwner()));
		list.remove(like);
		if(list.isEmpty())
			return;

		sort(list, 0, list.size() - 1);

		for(int i = 0; i < (list.size() > 8 ? 8 : list.size()); i++){
			OldLike get = list.get(i);
			ItemStack item = newItem(OtherLike, get.getLore());
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			String world = get.getWorld().getName();
			lore.add("§aワールド:§f " + (Worlds.containsKey(world) ? Worlds.get(world) : "Unknown"));
			lore.add("§a座標:§f X: " + get.getX() + ", Y: " + get.getY() + ", Z: " + get.getZ() + "");
			lore.add("§aお気に入り数:§f " + get.getLikeCount());
			meta.setLore(lore);
			item.setItemMeta(meta);
			inventory.setItem(10 + i, item);
		}
	}


	public static long getNumber(String s){
		String[] split = s.split("@");
		if(split.length < 2)
			return 0;

		return Long.parseLong(split[1]);
	}

	public static void create(Player player){
		UUID uuid = player.getUniqueId();
		if(!Worlds.containsKey(player.getWorld().getName())){
			tell(player, ChatColor.RED, "このワールドではLikeを作成出来ません。");
			return;
		}

		if(cooldown.contains(uuid)){
			tell(player, ChatColor.RED, "クールダウン中です。");
			return;
		}

		if(LikeMap.get(player.getLocation()).size() >= UpperLimit){
			tell(player, ChatColor.RED, "このチャンクではこれ以上Likeを作成出来ません。");
			return;
		}

		if(Mines.containsKey(uuid) && Mines.get(uuid).size() >= LimitConfig.get().getInt(uuid.toString())){
			tell(player, ChatColor.RED, "作成上限に達しているためこれ以上Likeを作成出来ません。");
			return;
		}

		NamedHologram hologram = new NamedHologram(player.getLocation().clone().add(0, 2, 0), String.valueOf(System.currentTimeMillis()));
		NamedHologramManager.addHologram(hologram);

		OldLike like = new OldLike(hologram, uuid);
		like.save(true);

		Likes.put(like.getId(), like);
		LikeMap.remove(like);
		addMine(like);
		LikeInvs.get(uuid).addMine(like);

		tell(player, ChatColor.GREEN, "Likeを作成しました。");

		cooldown.add(uuid);
		new BukkitRunnable(){
			@Override
			public void run(){
				cooldown.remove(uuid);
			}
		}.runTaskLater(OldMain.getPlugin(), CooldownTime);
	}

	public static void changeLore(OldLike like, String lore){
		like.editLore(lore);
		refreshHandler(like);
		update(like, false);
	}

	public static void changeOwner(OldLike like, UUID newOwner){
		removeMine(like);
		UUID oldOwner = like.getOwner();
		if(LikeInvs.containsKey(oldOwner))
			LikeInvs.get(oldOwner).removeMine(like);

		like.setOwner(newOwner);

		addMine(like);
		if(LikeInvs.containsKey(newOwner))
			LikeInvs.get(newOwner).addMine(like);

		update(like, false);
	}

	public static void move(OldLike like, Location loc){
		NamedHologram hologram = like.getHologram();

		LikeMap.remove(like);

		hologram.teleport(loc.clone().add(0, 2, 0));
		hologram.despawnEntities();
		like.refresh();
		refreshHandler(like);

		LikeMap.put(like);

		update(like, false);

		like.save(true);
	}

	public static void status(Player player, boolean me){
		LikeInvs invs = LikeInvs.get(player.getUniqueId());
		player.openInventory(me ? invs.firstMine() : invs.firstLike());
	}

	public static void favorite(Player player, OldLike like){
		UUID uuid = player.getUniqueId();

		LikeInvs.get(uuid).addLike(like);
		MyLikes.get(uuid).remove(like);

		like.incrementLikeCount();
		refreshHandler(like);
	}

	public static void unfavorite(Player player, OldLike like){
		UUID uuid = player.getUniqueId();

		LikeInvs.get(uuid).removeLike(like);
		MyLikes.get(uuid).remove(like);

		like.decrementLikeCount();
		refreshHandler(like);
	}

	public static void nonSaveDelete(OldLike like){
		UUID owner = like.getOwner();
		if(Mines.containsKey(owner))
			Mines.get(owner).remove(like);

		update(like, true);

		LikeConfig.get().set(like.getStringId(), null);
		LikeMap.remove(like);
		Likes.remove(like.getId());

		NamedHologram hologram = like.getHologram();
		hologram.delete();
		NamedHologramManager.removeHologram(hologram);
		HologramDatabase.deleteHologram(hologram.getName());
	}

	public static void delete(OldLike like){
		nonSaveDelete(like);
		HologramDatabase.trySaveToDisk();
	}

	public static void update(OldLike like, boolean delete){
		Bukkit.getScheduler().runTaskAsynchronously(OldMain.getPlugin(), new Runnable(){

			@Override
			public void run() {
				UUID owner = like.getOwner();
				if(LikeInvs.containsKey(owner)){
					LikeInvs invs = LikeInvs.get(owner);
					invs.removeMine(like);
					if(!delete)
						invs.addMine(like);
				}

				for(Entry<UUID, LikeInvs> entry : LikeInvs.entrySet()){
					if(entry.getKey().equals(owner))
						continue;

					LikeInvs inv = entry.getValue();
					inv.removeLike(like);
					if(!delete)
						inv.addLike(like);
				}

				if(delete) for(LikeMap map : MyLikes.values()){
					map.remove(like);
				}
			}

		});
	}

	public static void refreshHandler(OldLike like){
		like.getHologram().refreshAll();
		OldMain.applyTouchHandler(like, true);
		OldMain.applyTouchHandler(like, false);
	}

	public static TextComponent createInviteButton(String message, OldLike like){
		TextComponent component = new TextComponent(message);
		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/like " + TOKEN + " " + like.getStringId()));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.GRAY + "クリックでLikeにTP！")}));
		return component;
	}

}
