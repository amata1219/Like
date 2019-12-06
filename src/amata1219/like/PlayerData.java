package amata1219.like;

import amata1219.like.chunk.LikeMap;

public class PlayerData {
	
	public final LikeMap myLikes;
	public final LikeMap favoriteLikes;
	
	public PlayerData(LikeMap myLikes, LikeMap favoriteLikes){
		this.myLikes = myLikes;
		this.favoriteLikes = favoriteLikes;
	}
	
	public void favoriteLike(Like like){
		favoriteLikes.put(like);
	}
	
	public void unfavoriteLike(Like like){
		favoriteLikes.remove(like);
	}

}
