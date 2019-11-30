package amata1219.like.chunk;

import java.util.stream.Collectors;

import amata1219.like.OldLike;

public class LikeMap extends ChunkMap<OldLike> {
	
	/*
	 * constructor LikeMap(UUID)
	 */
	
	public boolean contains(OldLike like){
		return get(like.getX(), like.getZ()).contains(like);
	}
	
	public void put(OldLike like){
		put(like.getX(), like.getY(), like);
	}
	
	public void remove(OldLike like){
		remove(like.getX(), like.getY(), like);
	}
	
	@Override
	public String toString(){
		return values().stream()
				.map(OldLike::getId)
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
	
}
