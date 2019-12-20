package amata1219.like.player;

import java.util.HashMap;

import amata1219.like.Like;

public class PlayerData {
	
	public final HashMap<Long, Like> likes = new HashMap<>(), favoriteLikes = new HashMap<>();
	
	public void registerLike(Like like){
		likes.put(like.id, like);
	}
	
	public void unregisterLike(Like like){
		likes.remove(like.id);
	}
	
	public void favoriteLike(Like like){
		favoriteLikes.put(like.id, like);
	}
	
	public void unfavoriteLike(Like like){
		favoriteLikes.remove(like.id);
	}
	
}
