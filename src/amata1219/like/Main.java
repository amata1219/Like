package amata1219.like;

import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.config.MainConfig;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private MainConfig config;
	
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
