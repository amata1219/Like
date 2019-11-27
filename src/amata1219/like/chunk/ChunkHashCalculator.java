package amata1219.like.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkHashCalculator {

	public static long calculate(Chunk chunk){
		return calculate(chunk.getX(), chunk.getZ());
	}

	public static long calculate(Location location){
		return calculate(location.getBlockX(), location.getBlockZ());
	}

	public static long calculate(long x, long z){
		return ((x >> 4) << 32) ^ (z >> 4);
	}
	
}
