package amata1219.like.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import amata1219.like.Main;
import amata1219.like.player.PlayerData;
import amata1219.like.player.PlayerDatabase;

public class LoadPlayerDataListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		PlayerData data = PlayerDatabase.loadExistingPlayerData(player.getUniqueId());
		Main.plugin().players.put(player, data);
	}

}
