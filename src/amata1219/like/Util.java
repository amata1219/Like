package amata1219.like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;

public class Util {

	public static Config Config;
	public static Config LikeConfig;
	public static Config PlayerConfig;

	public static HashMap<String, String> Worlds;
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

	public static HashMap<Long, Like> Likes = new HashMap<>();
	public static LikeMap likeMap;
	public static HashMap<UUID, List<Like>> Mines = new HashMap<>();
	public static HashMap<UUID, LikeMap> MyLikes = new HashMap<>();
	public static HashMap<UUID, LikeInvs> LikeInvs = new HashMap<>();
	public static HashMap<UUID, Like> edit = new HashMap<>();
	public static List<UUID> cooldown = new ArrayList<>();

	public static final String OP_PERMISSION = "like.likeop";

	public static void init(){
		Config = new Config("config");
		LikeConfig = new Config("like_data");
		PlayerConfig = new Config("player_data");

		HashMap<Long, Hologram> holograms = new HashMap<>();
		HologramsAPI.getHolograms(Main.getPlugin()).parallelStream()
		.forEach(hologram -> holograms.put(hologram.getCreationTimestamp(), hologram));

		FileConfiguration likeConfig = LikeConfig.get();
		likeConfig.getKeys(false).parallelStream()
		.map(likeConfig::getString)
		.map(s -> s.split(","))
		.map(s -> new Like(holograms.get(Long.parseLong(s[0])), UUID.fromString(s[1]), Integer.parseInt(s[2])))
		.forEach(like -> Likes.put(like.getId(), like));

		Stream<Like> stream = Likes.values().parallelStream();
		stream.forEach(Util::embedTouchHandler);
		stream.forEach(likeMap::registerLike);
		stream.forEach(Util::addMine);
	}

	public static void loadConfigValues(){
		FileConfiguration config = Config.get();

		config.getStringList("Worlds").parallelStream()
		.map(s -> s.split(":"))
		.forEach(s -> Worlds.put(s[0], color(s[1])));

		ConfigurationSection lines = config.getConfigurationSection("TextLines");
		Counter = lines.getString("Counter");
		Lore = lines.getString("Lore");
		Message = lines.getString("Message");

		Tip = config.getString("TIP");
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
	}

	public static void loadPlayerData(UUID uuid){
		MyLikes.put(uuid, new LikeMap(uuid));
		LikeInvs.put(uuid, new LikeInvs(uuid));
	}

	private static final StringBuilder fastBuilder = new StringBuilder();

	public static void savePlayerData(UUID uuid, boolean shouldUpdate, boolean fast){
		List<Like> likes = MyLikes.get(uuid).getLikes();
		if(likes.isEmpty())
			return;

		String data = null;
		if(fast){
			for(Like like : likes){
				fastBuilder.append(like.getCreationTimestamp())
				.append(',');
			}
			data = fastBuilder.toString();
			fastBuilder.setLength(0);
		}else{
			StringBuilder builder = new StringBuilder();
			for(Like like : likes){
				builder.append(like.getCreationTimestamp())
				.append(',');
			}
			data = builder.toString();
		}
		PlayerConfig.get().set(uuid.toString(), data);
		if(shouldUpdate)
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

	public static TouchableLine castTouchableLine(HologramLine line){
		return (TouchableLine) line;
	}

	public static void embedTouchHandler(Like like){
		TouchHandler handler = new TouchHandler(){

			@Override
			public void onTouch(Player player) {
				UUID uuid = player.getUniqueId();
				if(player.isSneaking()){
					if(like.isOwner(uuid))
						player.openInventory(createEditMenu(like));
					else
						player.openInventory(player.hasPermission(OP_PERMISSION) ? createAdminMenu(like) : createInfoMenu(like));
				}else{
					LikeMap map = MyLikes.get(uuid);
					if(like.isOwner(uuid)){
						tell(player, ChatColor.RED, "自分のLikeはお気に入りに登録出来ません。");
						return;
					}

					if(map.isRegisteredLike(like)){
						tell(player, ChatColor.RED, "このLikeは既にお気に入りに登録しています。");
						return;
					}

					register(like);
				}
			}

		};

		Hologram hologram = like.getHologram();
		for(int i = 0; i < hologram.size(); i++)
			castTouchableLine(hologram.getLine(i)).setTouchHandler(handler);
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

	public static void register(Like like){
		UUID uuid = like.getOwner();
		likeMap.registerLike(like);
		MyLikes.get(uuid).registerLike(like);
		LikeInvs.get(uuid).addLike(like);
	}

	public static void unregister(Like like){
		UUID uuid = like.getOwner();
		likeMap.unregisterLike(like);
		MyLikes.get(uuid).unregisterLike(like);
		LikeInvs.get(uuid).removeLike(like);
	}

	public static void create(Player player){
		UUID uuid = player.getUniqueId();
		if(cooldown.contains(uuid)){
			tell(player, ChatColor.RED, "クールダウン中です。");
			return;
		}

		if(likeMap.getChunkSize(player.getLocation()) >= UpperLimit){
			tell(player, ChatColor.RED, "このチャンクではこれ以上Likeを作成出来ません。");
		}

		Hologram hologram = HologramsAPI.createHologram(Main.getPlugin(), player.getLocation().clone().add(0, 2, 0));
		Like like = new Like(hologram, uuid);
		register(like);
	}

	public static void changeLore(Like like, String lore){
		like.getLore().setText(lore.replace(Like.PLACE_HOLDER_OF_PLAYER_NAME, getName(like.getOwner())));
		update(like, true);
	}

	public static void changeOwner(Like like, UUID newOwner){
		unregister(like);
		like.setOwner(newOwner);
		register(like);
		update(like, true);
	}

	public static void move(Like like, Location loc){
		unregister(like);
		like.getHologram().teleport(loc.clone().add(0, 2, 0));
		register(like);
		update(like, true);
	}

	public static void unfavorite(Player player, Like like){
		UUID uuid = player.getUniqueId();
		LikeInvs.get(uuid).removeLike(like);
		MyLikes.get(uuid).unregisterLike(like);
		like.decrementLikeCount();
	}

	public static void delete(Like like){
		like.getHologram().delete();
		update(like, false);
	}

	public static void update(Like like, boolean reAdd){
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable(){

			@Override
			public void run() {
				LikeInvs likeInvs = LikeInvs.get(like.getOwner());
				likeInvs.removeMine(like);
				if(reAdd)
					likeInvs.addMine(like);
				Stream<LikeInvs> stream = LikeInvs.values().parallelStream()
				.filter(invs -> invs.hasLike(like));
				stream.forEach(invs -> invs.removeLike(like));
				if(reAdd)
					stream.forEach(invs -> invs.addLike(like));
			}

		});
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
