package amata1219.like;

import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

import amata1219.like.config.MainConfig;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	private MainConfig config;
	private final Map<UUID, PlayerData> playermap = Maps.newHashMap();
	
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

}
