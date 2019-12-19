package amata1219.like.player;

import java.util.HashMap;

import amata1219.like.Like;
import amata1219.like.chunk.LikeMap;

public class PlayerData {
	
	public final LikeMap myLikes = new LikeMap();
	public final HashMap<Long, Like> favoriteLikes = new HashMap<>();
	//public final LikeMap favoriteLikes = new LikeMap();
	
}
