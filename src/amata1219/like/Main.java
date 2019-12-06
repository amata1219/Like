package amata1219.like;

import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

import amata1219.like.config.MainConfig;
import amata1219.like.monad.Option;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	/*
	 * player data config
	 * 
	 * player_config.string(uuid).split(",").map(to_id).forEach(likemap::add)
	 */
	
	private MainConfig config;
	private final Map<Long, Like> likes = Maps.newHashMap();
	private final Map<UUID, PlayerData> players = Maps.newHashMap();
	
	@Override
	public void onEnable(){
		instance = this;
		config = new MainConfig();
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
	
	public Option<Like> like(long id){
		return Option.of(likes.get(id));
	}

}
