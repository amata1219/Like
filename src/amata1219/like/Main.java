package amata1219.like;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	/*
	 * UI(Masquerade)
	 * Listener
	 * Command(Slash)
	 * Name→UUID, UUID→Name (MojangAPI)
	 * 
	 */
	
	private static Main plugin;
	
	@Override
	public void onEnable(){
		plugin = this;
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public static Main plugin(){
		return plugin;
	}

}
