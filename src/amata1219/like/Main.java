package amata1219.like;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.config.MainConfig;
import amata1219.like.config.PlayerFavoriteLikesConfig;
import amata1219.like.player.PlayerData;
import amata1219.like.player.PlayerDataLoading;
import amata1219.like.tuplet.Tuple;

public class Main extends JavaPlugin {
	
	private static Main instance;
	public static final String INVITATION_TOKEN = UUID.randomUUID().toString();
	
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
	private PlayerFavoriteLikesConfig playerDataConfig;
	public final HashMap<Long, Like> likes = new HashMap<>();
	public final HashMap<UUID, List<Like>> playerLikes = new HashMap<>();
	public final HashMap<Player, PlayerData> players = new HashMap<>();
	public final HashMap<UUID, Long> descriptionEditors = new HashMap<>();
	
	@Override
	public void onEnable(){
		instance = this;
		
		config = new MainConfig();
		playerDataConfig = new PlayerFavoriteLikesConfig();
		
		getServer().getOnlinePlayers().stream()
		.map(p -> Tuple.of(p, PlayerDataLoading.loadExistingPlayerData(p.getUniqueId())))
		.forEach(t -> players.put(t.first, t.second));
	}
	
	@Override
	public void onDisable(){
		players.entrySet().forEach(e -> playerDataConfig.save(e.getKey().getUniqueId(), e.getValue().favoriteLikes));
	}
	
	public static Main instance(){
		return instance;
	}
	
	public MainConfig config(){
		return config;
	}
	
	public PlayerFavoriteLikesConfig playerDataConfig(){
		return playerDataConfig;
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
