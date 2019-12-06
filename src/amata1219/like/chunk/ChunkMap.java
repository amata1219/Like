package amata1219.like.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import static amata1219.like.chunk.ChunkHashCalculator.*;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkMap<V> {
	
	private final HashMap<Long, Collection<V>> map = new HashMap<>();
	
	public boolean containsHash(Chunk chunk){
		return containsHash(chunk.getX(), chunk.getZ());
	}

	public boolean containsHash(Location location){
		return containsHash(location.getBlockX(), location.getBlockZ());
	}

	public boolean containsHash(int x, int z){
		return containsHash(calculate(x, z));
	}

	public boolean containsHash(long chunkHash){
		return map.containsKey(chunkHash);
	}
	
	public Collection<V> values(){
		return map.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
	
	public Collection<V> get(Chunk chunk){
		return get(chunk.getX(), chunk.getZ());
	}
	
	public Collection<V> get(Location location){
		return get(location.getBlockX(), location.getBlockZ());
	}

	public Collection<V> get(int x, int z){
		return get(calculate(x, z));
	}

	public Collection<V> get(long hash){
		return map.get(hash);
	}
	
	public void put(int x, int z, V value){
		final long hash = calculate(x, z);
		if(containsHash(hash)) map.get(hash).add(value);
		else map.put(hash, new ArrayList<>(Arrays.asList(value)));
	}

	public void remove(int x, int z, V value){
		final long hash = calculate(x, z);
		Collection<V> list = map.get(hash);
		list.remove(value);
		if(list.isEmpty()) map.remove(hash);
	}

}
