package amata1219.like.chunk;

import java.util.stream.Collectors;

import amata1219.like.Like;

public class LikeMap extends ChunkMap<Like> {
	
	@Override
	public String toString(){
		return values().stream()
				.map(Like::getId)
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}
	
}
