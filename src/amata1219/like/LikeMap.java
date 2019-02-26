package amata1219.like;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class LikeMap {


	private final HashMap<Long, List<Like>> likes = new HashMap<>();

	public LikeMap(){

	}

	public LikeMap(UUID uuid){
		String data = Util.PlayerConfig.get().getString(uuid.toString());
		if(data == null)
			return;

		Arrays.stream(data.split(",")).parallel()
		.map(Long::parseLong)
		.forEach(id -> registerLike(Util.Likes.get(id)));
	}

	public List<Like> getLikes(){
		List<Like> list = new ArrayList<>();
		likes.values().parallelStream().forEach(list::addAll);
		return list;
	}

	public List<Like> getLikes(int x, int z){
		long hash = toChunkHash(x, z);
		return likes.containsKey(hash) ? likes.get(hash) : Collections.emptyList();
	}

	public List<Like> getLikes(Chunk chunk){
		return getLikes(chunk.getX(), chunk.getZ());
	}

	public List<Like> getNearLikes(Like like){
		return getLikes(like.getX(), like.getZ());
	}

	public int getChunkSize(Location loc){
		return getLikes(loc.getBlockX(), loc.getBlockZ()).size();
	}

	public boolean isRegisteredChunk(Chunk chunk){
		return likes.containsKey(toChunkHash(chunk));
	}

	public boolean isRegisteredLike(Like like){
		return getNearLikes(like).contains(like);
	}

	public void registerLike(Like like){
		long hash = toChunkHash(like);
		List<Like> list = likes.get(like);
		if(list == null)
			likes.put(hash, list = new ArrayList<>());
		list.add(like);
	}

	public void unregisterLike(Like like){
		getLikes(like.getX(), like.getZ()).remove(like);
	}

	public void moveLike(Like like){
		registerLike(like);
		unregisterLike(like);
	}

	public void clear(){
		likes.clear();
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(Like like : getLikes())
			builder.append(String.valueOf(like.getId())).append(",");
		String data = builder.toString();
		return data.substring(0, data.length() - 1);
	}

	public static long toChunkHash(int x, int z){
		return (x >> 4) ^ (z << 28);
	}

	public static long toChunkHash(Chunk chunk){
		return toChunkHash(chunk.getX(), chunk.getZ());
	}

	public static long toChunkHash(Like hologram){
		return toChunkHash(hologram.getX(), hologram.getZ());
	}

	public static long toChunkHash(Location location){
		return toChunkHash(location.getBlockX(), location.getBlockZ());
	}

}
