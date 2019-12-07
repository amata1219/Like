package amata1219.like;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.config.MainConfig;
import amata1219.like.config.PlayerFavoriteLikesConfig;

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
	
	@Override
	public void onEnable(){
		instance = this;
		
		config = new MainConfig();
		playerFavoriteLikesConfig = new PlayerFavoriteLikesConfig();
	}
	
	@Override
	public void onDisable(){
		
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
