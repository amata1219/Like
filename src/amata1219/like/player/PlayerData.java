package amata1219.like.player;

import java.util.HashMap;

import amata1219.like.Like;

public class PlayerData {
	
	public final HashMap<Long, Like> likes = new HashMap<>(), favoriteLikes = new HashMap<>();
	
	public void addLike(Like like){
		likes.put(like.id, like);
	}
	
	public void addFavoriteLike(Like like){
		favoriteLikes.put(like.id, like);
	}
	
}
