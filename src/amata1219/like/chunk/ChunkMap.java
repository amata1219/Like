package amata1219.like.chunk;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkMap<V> {

	private final HashMap<Long, List<V>> map = new HashMap<>();
	
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
		return get(hash(x, z));
	}

	public List<V> get(long hash){
		return map.getOrDefault(hash, Collections.emptyList());
	}
	
	public void put(long x, long z, V value){
		long hash = hash(x, z);
		map.computeIfAbsent(hash, k -> new ArrayList<>()).add(value);
	}

	public void remove(long x, long z, V value){
		long hash = hash(x, z);
		if (!map.containsKey(hash)) return;

		List<V> list = map.get(hash);
		list.remove(value);

		if(list.isEmpty()) map.remove(hash);
	}

	public static long hash(long x, long z) {
		long chunkX = x >> 4, chunkZ = z >> 4;
		return (chunkX << 32) ^ chunkZ;
	}

}
