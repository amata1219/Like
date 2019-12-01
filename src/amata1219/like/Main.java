package amata1219.like;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	@Override
	public void onEnable(){
		instance = this;
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public static Main instance(){
		return instance;
	}

}
