package amata1219.like.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkMap<V> {
	
	private final HashMap<Long, Collection<V>> map = new HashMap<>();
	
	public boolean containsHash(Chunk chunk){
		return containsHash(calculate(chunk));
	}

	public boolean containsHash(Location location){
		return containsHash(calculate(location));
	}

	public boolean containsHash(int x, int z){
		return containsHash(calculate(x, z));
	}

	public boolean containsHash(long chunkHash){
		return map.containsKey(chunkHash);
	}
	
	public Collection<V> getAll(Location lesserBoundaryCorner, Location greaterBoundaryCorner){
		return getAll(calculateAll(lesserBoundaryCorner, greaterBoundaryCorner));
	}

	public Collection<V> getAll(int lesserBoundaryX, int lesserBoundaryZ, int greaterBoundaryX, int greaterBoundaryZ){
		return getAll(calculateAll(lesserBoundaryX, lesserBoundaryZ, greaterBoundaryX, greaterBoundaryZ));
	}

	public Collection<V> getAll(Collection<Long> chunkHashes){
		return chunkHashes.stream()
				.map(map::get)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
	
	public Collection<V> values(){
		return getAll(map.keySet());
	}
	
	private void put(long chunkHash, V value){
		if(containsHash(chunkHash)) map.get(chunkHash).add(value);
		else map.put(chunkHash, new ArrayList<>(Arrays.asList(value)));
	}

	public void putAll(Location lesserBoundaryCorner, Location greaterBoundaryCorner, V value){
		putAll(calculateAll(lesserBoundaryCorner, greaterBoundaryCorner), value);
	}

	public void putAll(int lesserBoundaryX, int lesserBoundaryZ, int greaterBoundaryX, int greaterBoundaryZ, V value){
		putAll(calculateAll(lesserBoundaryX, lesserBoundaryZ, greaterBoundaryX, greaterBoundaryZ), value);
	}

	public void putAll(Collection<Long> chunkHashes, V value){
		chunkHashes.forEach(hash -> put(hash, value));
	}

	private void remove(long chunkHash, V value){
		Collection<V> list = map.get(chunkHash);
		list.remove(value);
		if(list.isEmpty()) map.remove(chunkHash);
	}

	public void removeAll(Location lesserBoundaryCorner, Location greaterBoundaryCorner, V value){
		removeAll(calculateAll(lesserBoundaryCorner, greaterBoundaryCorner), value);
	}

	public void removeAll(int lesserBoundaryX, int lesserBoundaryZ, int greaterBoundaryX, int greaterBoundaryZ, V value){
		removeAll(calculateAll(lesserBoundaryX, lesserBoundaryZ, greaterBoundaryX, greaterBoundaryZ), value);
	}

	public void removeAll(Collection<Long> chunkHashes, V value){
		chunkHashes.forEach(hash -> remove(hash, value));
	}
	
	private long calculate(Chunk chunk){
		return calculate(chunk.getX(), chunk.getZ());
	}

	private long calculate(Location location){
		return calculate(location.getBlockX(), location.getBlockZ());
	}

	private long calculate(long x, long z){
		return ((x >> 4) << 32) ^ (z >> 4);
	}

	private Collection<Long> calculateAll(Location lesserBoundaryCorner, Location greaterBoundaryCorner){
		return calculateAll(lesserBoundaryCorner.getBlockX(), lesserBoundaryCorner.getBlockZ(), greaterBoundaryCorner.getBlockX(), greaterBoundaryCorner.getBlockZ());
	}

	private Collection<Long> calculateAll(long lesserBoundaryX, long lesserBoundaryZ, long greaterBoundaryX, long greaterBoundaryZ){
		long chunkX = lesserBoundaryX >> 4, limitX = greaterBoundaryX >> 4;
		long chunkZ = lesserBoundaryZ >> 4, limitZ = greaterBoundaryZ >> 4;
		long initialCapacity = Math.min((limitX - chunkX), 1) * Math.min((limitZ - chunkZ), 1);
		List<Long> hashes = new ArrayList<>((int) initialCapacity);
		for(long x = chunkX; x <= limitX; x++)
			for(long z = chunkZ; z <= limitZ; z++)
				hashes.add((x << 32) ^ z);
		return hashes;
	}


}
