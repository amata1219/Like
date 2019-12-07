package amata1219.like;

import java.util.Collections;
import java.util.UUID;

import amata1219.like.chunk.LikeMap;

public class PlayerData {
	
	private final Main plugin = Main.instance();
	
	public final LikeMap myLikes = new LikeMap();
	public final LikeMap favoriteLikes = new LikeMap();
	
	public PlayerData(UUID uuid){
		plugin.playerLikes.getOrDefault(uuid, Collections.emptyList()).forEach(myLikes::put);
		this.favoriteLikes = favoriteLikes;
	}

}
