package amata1219.like.playerdata;

import java.util.HashMap;

import amata1219.like.Like;

public class PlayerData {
	
	public final HashMap<Long, Like> likes = new HashMap<>(), favoriteLikes = new HashMap<>();
	
	public boolean isRegisteredLike(Like like){
		return likes.containsKey(like.id);
	}
	
	public void registerLike(Like like){
		if(like != null) likes.put(like.id, like);
	}
	
	public void unregisterLike(Like like){
		if(like != null) likes.remove(like.id);
	}
	
	public boolean isFavoriteLike(Like like){
		return favoriteLikes.containsKey(like.id);
	}
	
	public void favoriteLike(Like like){
		if(like != null) favoriteLikes.put(like.id, like);
	}
	
	public void unfavoriteLike(Like like){
		if(like != null) favoriteLikes.remove(like.id);
	}
	
}
