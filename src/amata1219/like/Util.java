package amata1219.like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

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
import com.gmail.filoghost.holographicdisplays.exception.HologramNotFoundException;
import com.gmail.filoghost.holographicdisplays.exception.InvalidFormatException;
import com.gmail.filoghost.holographicdisplays.exception.WorldNotFoundException;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Util {

	public static Config Config;
	public static Config LikeConfig;
	public static Config PlayerConfig;

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
	public static Material ChangeOwner;
	public static Material PageButton;
	public static Material LikeIcon;
	public static double Tp;
	public static double Invite;
	public static int Range;
	public static String InviteMessage;

	public static HashMap<Long, Like> Likes = new HashMap<>();
	public static LikeMap LikeMap = new LikeMap();
	public static HashMap<UUID, List<Like>> Mines = new HashMap<>();

	public static HashMap<UUID, LikeMap> MyLikes = new HashMap<>();
	public static HashMap<UUID, LikeInvs> LikeInvs = new HashMap<>();

	public static HashMap<UUID, Like> edit = new HashMap<>();
	public static List<UUID> cooldown = new ArrayList<>();

	public static final String TOKEN = String.valueOf(System.nanoTime());

	public static final String OP_PERMISSION = "like.likeop";

	public static void init(){
		Config = new Config("config");
		LikeConfig = new Config("like_data");
		PlayerConfig = new Config("player_data");

		loadConfigValues();

		//NamedHologramManager.getHologram(key) = null
		FileConfiguration config = LikeConfig.get();
		for(String name: config.getKeys(false)){
			NamedHologram hologram = null;
			try {
				hologram = HologramDatabase.loadHologram(name);
			} catch (HologramNotFoundException | InvalidFormatException | WorldNotFoundException e) {
				e.printStackTrace();
			}
			if(hologram == null){
				System.out.println("Hologram is not found -> " + name);
				continue;
			}
			long id = Long.parseLong(name);
			String[] data = config.getString(name).split(",");
			Like like = new Like(hologram, UUID.fromString(data[0]), Integer.parseInt(data[1]));
			Main.applyTouchHandler(like, false);
			Likes.put(id, like);
			LikeMap.registerLike(like);
			addMine(like);
		}
	}

	public static void unload(){
		for(Like like : Likes.values()){
			like.save();
			LikeConfig.get().set(like.getStringId(), like.toString());
			like.getHologram().despawnEntities();
		}
		LikeConfig.update();
		HologramDatabase.trySaveToDisk();
	}

	public static void loadConfigValues(){
		FileConfiguration config = Config.get();

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
		ChangeOwner = type(items.getString("ChangeOwner"));
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
	}

	public static void savePlayerData(UUID uuid, boolean update){
		String data = null;
		StringBuilder builder = new StringBuilder();
		if(Mines.containsKey(uuid)){
			for(Like like : Mines.get(uuid)){
				builder.append(like.getStringId());
				builder.append(",");
			}
			data = builder.length() > 0 ? builder.substring(0, builder.length() - 1).toString() : builder.toString();
		}
		PlayerConfig.get().set(uuid.toString(), data);
		if(update)
			PlayerConfig.update();
	}

	public static void unloadPlayerData(UUID uuid){
		MyLikes.remove(uuid);
		LikeInvs.remove(uuid);
	}

	public static void addMine(Like like){
		UUID owner = like.getOwner();
		List<Like> list = Mines.get(owner);
		if(list == null)
			Mines.put(owner, list = new ArrayList<>());
		list.add(like);
	}

	public static void removeMine(Like like){
		UUID owner = like.getOwner();
		List<Like> list = Mines.get(owner);
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

	public static int toInt(double d){
		return Double.valueOf(d).intValue();
	}

	public static TextLine castTextLine(HologramLine line){
		return (TextLine) line;
	}

	public static Inventory createInventory(int size, String title){
		return Bukkit.createInventory(null, size, ChatColor.DARK_GRAY + title);
	}

	public static Inventory createInfoMenu(Like like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(18, "Info@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.WHITE + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(1, owner);

		inventory.setItem(4, newItem(LikeCount, "お気に入りの数:" + like.getLikeCount()));
		inventory.setItem(5, newItem(Timestamp, "作成日時: " + like.getCreationTimestamp()));
		inventory.setItem(6, newItem(Id, "管理ID: " + like.getId()));
		inventory.setItem(7, newItem(Unfavorite, "お気に入りの解除"));
		inventory.setItem(9, newItem(OtherLike, "この作者の他のLike情報"));

		setOtherLike(inventory, like);
		return inventory;
	}

	public static Inventory createEditMenu(Like like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(9, "Edit@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.WHITE + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(1, owner);

		inventory.setItem(3, newItem(LikeCount, "お気に入りの数:" + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "作成日時: " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "管理ID: " + like.getId()));
		inventory.setItem(6, newItem(Edit, "表示内容の編集"));
		inventory.setItem(7, newItem(Remove1, "Likeの削除"));
		return inventory;
	}

	public static Inventory createConfirmMenu(Like like){
		Inventory inventory = createInventory(9, "Remove@" + like.getStringId());
		inventory.setItem(4, newItem(Remove2, ChatColor.RED + "Likeを削除する(※元に戻せません)"));
		return inventory;
	}

	public static Inventory createAdminMenu(Like like){
		UUID uuid = like.getOwner();
		Inventory inventory = createInventory(18, "Admin@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.WHITE + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(1, owner);

		inventory.setItem(3, newItem(LikeCount, "お気に入りの数:" + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "作成日時: " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "管理ID: " + like.getId()));
		inventory.setItem(6, newItem(Edit, "表示内容の編集"));
		inventory.setItem(7, newItem(Remove1, "Likeの削除"));
		inventory.setItem(9, newItem(OtherLike, "この作者の他のLike情報"));

		setOtherLike(inventory, like);
		return inventory;
	}

	public static void setOtherLike(Inventory inventory, Like like){
		List<Like> list = Mines.get(like.getOwner());
		if(list == null)
			return;

		sort(list, 0, list.size() - 1);

		for(int i = 0; i < (list.size() > 8 ? 8 : list.size()); i++){
			ItemStack item = newItem(OtherLike, like.getLore().getText());
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			String world = like.getWorld().getName();
			lore.add(ChatColor.GRAY + "ワールド: " + (Worlds.containsKey(world) ? Worlds.get(world) : "Unknown"));
			lore.add(ChatColor.GRAY + "座標: (X: " + like.getX() + ", Y: " + like.getY() + ", Z: " + like.getZ() + ")");
			lore.add(ChatColor.GRAY + "お気に入り数: " + like.getLikeCount());
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}

	public static ItemStack newItem(Material material, String displayName){
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + displayName);
		item.setItemMeta(meta);
		return item;
	}

	public static long getNumber(String s){
		String[] split = s.split("@");
		if(split.length < 2)
			return 0;

		return Long.parseLong(split[1]);
	}

	public static void register(Like like, boolean me){
		UUID uuid = like.getOwner();
		Likes.put(like.getId(), like);
		LikeMap.registerLike(like);
		LikeInvs invs = LikeInvs.get(uuid);
		if(me){
			addMine(like);
			invs.addMine(like);
		}else{
			MyLikes.get(uuid).registerLike(like);
			invs.addLike(like);
		}
	}

	public static void unregister(Like like, boolean me){
		UUID uuid = like.getOwner();
		LikeInvs invs = LikeInvs.get(uuid);
		if(me){
			removeMine(like);
			invs.removeMine(like);
		}else{
			MyLikes.get(uuid).unregisterLike(like);
			invs.removeLike(like);
		}
		LikeMap.unregisterLike(like);
		Likes.remove(like.getId());
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

		if(LikeMap.getChunkSize(player.getLocation()) >= UpperLimit){
			tell(player, ChatColor.RED, "このチャンクではこれ以上Likeを作成出来ません。");
			return;
		}

		NamedHologram hologram = new NamedHologram(player.getLocation().clone().add(0, 2, 0), String.valueOf(System.currentTimeMillis()));
		NamedHologramManager.addHologram(hologram);
		HologramDatabase.saveHologram(hologram);
		HologramDatabase.trySaveToDisk();
		Like like = new Like(hologram, uuid);
		register(like, true);
		tell(player, ChatColor.GREEN, "Likeを作成しました。");
		cooldown.add(uuid);
		new BukkitRunnable(){
			@Override
			public void run(){
				cooldown.remove(uuid);
			}
		}.runTaskLater(Main.getPlugin(), CooldownTime);
	}

	public static void changeLore(Like like, String lore){
		like.getLore().setText(lore.replace(Util.PLACE_HOLDER_OF_PLAYER_NAME, getName(like.getOwner())));
		update(like, false);
		refresh(like);
	}

	public static void changeOwner(Like like, UUID newOwner){
		unregister(like, true);
		like.setOwner(newOwner);
		register(like, true);
		update(like, false);
	}

	public static void move(Like like, Location loc){
		unregister(like, true);
		like.getHologram().teleport(loc.clone().add(0, 2, 0));
		register(like, true);
		update(like, false);
		refresh(like);
	}

	public static void status(Player player, boolean me){
		LikeInvs invs = LikeInvs.get(player.getUniqueId());
		player.openInventory(me ? invs.firstMine() : invs.firstLike());
	}

	public static void unfavorite(Player player, Like like){
		UUID uuid = player.getUniqueId();
		LikeInvs.get(uuid).removeLike(like);
		MyLikes.get(uuid).unregisterLike(like);
		like.decrementLikeCount();
		refresh(like);
	}

	public static void refresh(Like like){
		like.getHologram().refreshAll();
		Main.applyTouchHandler(like, true);
		Main.applyTouchHandler(like, false);
	}

	public static void delete(Like like){
		UUID owner = like.getOwner();
		if(Mines.containsKey(owner))
			Mines.get(owner).remove(like);

		update(like, true);

		final String id = like.getStringId();
		LikeConfig.get().set(id, null);
		Likes.remove(like.getId());
		NamedHologram hologram = like.getHologram();
		hologram.despawnEntities();
		HologramDatabase.deleteHologram(id);
		hologram.delete();
	}

	public static void update(Like like, boolean delete){
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable(){

			@Override
			public void run() {
				UUID owner = like.getOwner();
				LikeInvs invs = LikeInvs.get(owner);
				invs.removeMine(like);
				if(!delete)
					invs.addMine(like);

				for(Entry<UUID, LikeInvs> entry : LikeInvs.entrySet()){
					if(entry.getKey().equals(owner))
						continue;

					LikeInvs inv = entry.getValue();
					inv.removeLike(like);
					if(!delete)
						inv.addLike(like);
				}

				if(delete) for(LikeMap map : MyLikes.values()){
					map.unregisterLike(like);
				}
			}

		});
	}

	public static TextComponent createInviteButton(String message, Like like){
		TextComponent component = new TextComponent(message);
		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/like " + TOKEN + " " + like.getStringId()));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(ChatColor.GRAY + "クリックでLikeにTP！")}));
		return component;
	}

	public static void sort(List<Like> list, int left, int right){
		if(left >= right)
			return;

		int p = list.get((left + right) / 2).getLikeCount();
		int l = left, r = right;
		Like tmp = null;
		while(l <= r){
			while(list.get(l).getLikeCount() > p)
				l++;
			while(list.get(r).getLikeCount() < p)
				r++;
			if(l > r)
				continue;

			tmp = list.get(l);
			list.set(l, list.get(r));
			list.set(r, tmp);
		}

		sort(list, left, r);
		sort(list, l, right);
	}

}
