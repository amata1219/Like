package amata1219.like.chunk;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkMap<V> {
	
	private final HashMap<Long, List<V>> map = new HashMap<>();
	
	public boolean containsHash(long x, long z){
		return containsHash(calculate(x, z));
	}

	public boolean containsHash(long chunkHash){
		return map.containsKey(chunkHash);
	}
	
	public List<V> values(){
		return map.values().stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	public List<V> get(Chunk chunk){
		return get(chunk.getX(), chunk.getZ());
	}
	
	public List<V> get(Location location){
		return get(location.getBlockX(), location.getBlockZ());
	}

	public List<V> get(long x, long z){
		return get(calculate(x, z));
	}

	public List<V> get(long hash){
		return map.getOrDefault(hash, Collections.emptyList());
	}
	
	public void put(long x, long z, V value){
		final long hash = calculate(x, z);
		if(containsHash(hash)) map.get(hash).add(value);
		else map.put(hash, new ArrayList<>(Collections.singletonList(value)));
	}

	public void remove(long x, long z, V value){
		final long hash = calculate(x, z);
		List<V> list = map.get(hash);
		list.remove(value);
		if(list.isEmpty()) map.remove(hash);
	}

	private static long calculate(long x, long z){
		return ((x >> 4) << 32) ^ (z >> 4);
	}

}
