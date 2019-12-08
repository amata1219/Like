package amata1219.like.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import amata1219.like.Main;
import amata1219.like.player.PlayerData;

public class UnloadPlayerDataListener implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Main plugin = Main.instance();
		Player player = e.getPlayer();
		PlayerData data = plugin.players.remove(player);
		plugin.playerDataConfig().save(player.getUniqueId(), data.favoriteLikes);
	}

}
