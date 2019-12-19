package amata1219.like.player;

import java.util.Collections;
import java.util.UUID;

import amata1219.like.Main;

public class PlayerDataLoading {
	
	public static PlayerData loadExistingPlayerData(UUID uuid){
		Main plugin = Main.plugin();
		PlayerData data = new PlayerData();
		plugin.playerLikes.getOrDefault(uuid, Collections.emptyList()).forEach(data.myLikes::put);
		plugin.playerDataConfig().favoriteLikes(uuid).forEach(data.favoriteLikes::put);
		return data;
	}

}
