package amata1219.like.chunk;

import java.util.stream.Collectors;

import amata1219.like.Like;

public class LikeMap extends ChunkMap<Like> {
	
	public void put(Like like){
		put(like.x(), like.z(), like);
	}
	
	public void remove(Like like){
		remove(like.x(), like.z(), like);
	}
	
	public boolean contains(Like like){
		return get(like.x(), like.z()).contains(like);
	}
	
	@Override
	public String toString(){
		return values().stream()
				.map(l -> l.id)
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
	
}
