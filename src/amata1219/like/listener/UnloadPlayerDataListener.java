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
		Main plugin = Main.plugin();
		Player player = e.getPlayer();
		PlayerData data = plugin.players.remove(player);
		plugin.playerConfig().save(player.getUniqueId(), data.favoriteLikes);
	}

}
