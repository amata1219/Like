package amata1219.like.chunk;

import java.util.stream.Collectors;

import amata1219.like.Like;

public class LikeMap extends ChunkMap<Like> {
	
	/*
	 * constructor LikeMap(UUID)
	 */
	
	public boolean contains(Like like){
		return get(like.getX(), like.getZ()).contains(like);
	}
	
	public void put(Like like){
		put(like.getX(), like.getY(), like);
	}
	
	public void remove(Like like){
		remove(like.getX(), like.getY(), like);
	}
	
	@Override
	public String toString(){
		return values().stream()
				.map(Like::getId)
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
	
}
