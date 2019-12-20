package amata1219.like;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.config.LikeDatabase;
import amata1219.like.config.MainConfig;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.enchantment.GleamEnchantment;
import amata1219.like.masquerade.listener.UIListener;
import amata1219.like.monad.Maybe;
import amata1219.like.player.PlayerData;
import amata1219.like.player.PlayerDatabase;
import amata1219.like.reflection.Field;
import amata1219.like.reflection.SafeCast;
import amata1219.like.tuplet.Tuple;

public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public static final String INVITATION_TOKEN = UUID.randomUUID().toString();
	public static final String OPERATOR_PERMISSION = "like.likeop";
	
	public static Main plugin(){
		return plugin;
	}
	
	/*
	 * player data config
	 * 
	 * player_config.string(uuid).split(",").map(to_id).forEach(likemap::add)
	 * 
	 * onEnable {
	 * 
	 * make configs
	 * load likes
	 * load online players' data
	 * 
	 * }
	 * 
	 * プレイヤーデータ
	 * 
	 * ・ログイン時にロード
	 * ・ログアウト時にセーブ・アンロード
	 * 
	 * Like
	 * 
	 * ・onEnableでロード → mines: map[uuid, list[like]]にセット
	 * ・onDisableでアンロード
	 * 
	 * MyLike
	 * 
	 * 
	 * 
	 */
	
	private MainConfig config;
	private LikeDatabase likeDatabase;
	private PlayerDatabase playerDatabase;
	
	public final HashMap<Long, Like> likes = new HashMap<>();
	//public final HashMap<UUID, List<Like>> playerLikes = new HashMap<>();
	public final HashMap<UUID, PlayerData> players = new HashMap<>();
	
	public final HashMap<UUID, Long> descriptionEditors = new HashMap<>();
	public final HashSet<UUID> cooldownMap = new HashSet<>();
	
	@Override
	public void onEnable(){
		plugin = this;
		
		getServer().getPluginManager().registerEvents(new UIListener(), this);

		Field<Enchantment, Boolean> acceptingNew = Field.of(Enchantment.class, "acceptingNew");
		acceptingNew.set(null, true);
		try{
			Enchantment.registerEnchantment(GleamEnchantment.INSTANCE);
		}catch(Exception e){
			
		}finally{
			acceptingNew.set(null, false);
		}
		
		config = new MainConfig();
		
		likeDatabase = new LikeDatabase();
		Tuple<HashMap<Long, Like>, HashMap<UUID, List<Like>>> maps = likeDatabase.load();
		maps.first.forEach((id, like) -> likes.put(id, like));
		
		playerDatabase = new PlayerDatabase();
		playerDatabase.load(maps.second).forEach((uuid, data) -> players.put(uuid, data));
	}
	
	@Override
	public void onDisable(){
		playerDatabase.save();
		likeDatabase.save();
		
		getServer().getOnlinePlayers().forEach(player -> {
			Maybe.unit(player.getOpenInventory())
			.map(InventoryView::getTopInventory)
			.map(Inventory::getHolder)
			.flatMap(x -> SafeCast.cast(x, Layout.class))
			.apply(x -> player.closeInventory());
		});

		HandlerList.unregisterAll(this);
	}
	
	public MainConfig config(){
		return config;
	}
	
	public PlayerDataLoading playerDatabase(){
		return playerDatabase;
	}
	
	public List<Like> likes(UUID uuid){
		return playerLikes.getOrDefault(uuid, Collections.emptyList());
	}
	
	public void deleteLike(Like like){
		UUID uuid = like.owner();
		
		Collection<Like> likes = playerLikes.get(uuid);
		likes.remove(like);
		if(likes.isEmpty()) playerLikes.remove(uuid);
		
		Player player = getServer().getPlayer(uuid);
		if(player != null && player.isOnline()) players.get(player).myLikes.remove(like);
		
		players.values().stream()
		.map(d -> d.favoriteLikes)
		.forEach(m -> m.remove(like));
		
		this.likes.remove(like);
	}

}
