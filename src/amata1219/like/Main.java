package amata1219.like;

import java.util.Collection;
import java.util.HashMap;
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
	private PlayerFavoriteLikesConfig playerFavoriteLikesConfig;
	public final HashMap<Long, Like> likes = new HashMap<>();
	public final HashMap<UUID, Collection<Like>> playerLikes = new HashMap<>();
	public final HashMap<UUID, PlayerData> players = new HashMap<>();
	public final HashMap<Player, Like> descriptionEditors = new HashMap<>();
	
	@Override
	public void onEnable(){
		instance = this;
		
		config = new MainConfig();
		playerFavoriteLikesConfig = new PlayerFavoriteLikesConfig();
		
		getServer().getOnlinePlayers().stream()
		.map(Player::getUniqueId)
		.map(u -> Tuple.of(u, PlayerDataLoading.loadExistingPlayerData(u)))
		.forEach(t -> players.put(t.first, t.second));
	}
	
	@Override
	public void onDisable(){
		players.entrySet().forEach(e -> playerFavoriteLikesConfig.save(e.getKey(), e.getValue().favoriteLikes));
	}
	
	public static Main instance(){
		return instance;
	}
	
	public MainConfig config(){
		return config;
	}
	
	public PlayerFavoriteLikesConfig playerFavoriteLikesConfig(){
		return playerFavoriteLikesConfig;
	}

}
