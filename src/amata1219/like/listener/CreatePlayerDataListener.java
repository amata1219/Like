package amata1219.like.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import amata1219.like.Main;
import amata1219.like.playerdata.PlayerData;

public class CreatePlayerDataListener implements Listener {
	
	private final Main plugin = Main.plugin();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		if (!plugin.players.containsKey(uuid)) plugin.players.put(uuid, new PlayerData());
	}

}
