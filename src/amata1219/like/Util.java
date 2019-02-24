package amata1219.like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	public static HashMap<UUID, List<Like>> Mines = new HashMap<>();
	public static HashMap<UUID, LikeMap> MyLikes = new HashMap<>();
	public static HashMap<UUID, LikeInvs> LikeInvs = new HashMap<>();

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

		Likes.values().parallelStream().forEach(Util::embedTouchHandler);

		Likes.values().parallelStream().forEach(Util::addMine);
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
					if(like.isOwner(uuid)){
						//edit
					}else{
						if(player.hasPermission(OP_PERMISSION)){
							//admin
						}else{
							//info
						}
					}
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
					register(player, like);
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
		Inventory inventory = createInventory(18, like.getStringId());

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
		return null;
	}

	public static Inventory createEditMenu(Like like){
		return null;
	}

	public static Inventory createConfirmMenu(){
		return null;
	}

	public static Inventory createAdminMenu(){
		return null;
	}

	public static ItemStack newItem(Material material, String displayName){
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + displayName);
		item.setItemMeta(meta);
		return item;
	}

	public static void register(Player player, Like like){
		UUID uuid = player.getUniqueId();
		MyLikes.get(uuid).registerlLike(like);
		LikeInvs.get(uuid).addLike(like);
	}

	public static void unregister(Player player, Like like){
		UUID uuid = player.getUniqueId();
		MyLikes.get(uuid).unregisterLike(like);
		LikeInvs.get(uuid).removeLike(like);
	}

	public static void create(){

	}

	public static void changeLore(){

	}

	public static void changeOwner(){

	}

	public static void move(){

	}

	public static void delete(){

	}

}
