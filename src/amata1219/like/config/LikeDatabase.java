package amata1219.like.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;

import amata1219.like.Like;
import amata1219.like.tuplet.Tuple;

public class LikeDatabase extends Config {

	public LikeDatabase() {
		super("like_data.yml");
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException();
	}
	
	public Tuple<HashMap<Long, Like>, HashMap<UUID, List<Like>>> readAll(){
		FileConfiguration config = config();
		HashMap<Long, Like> likes = new HashMap<>();
		HashMap<UUID, List<Like>> playerLikes = new HashMap<>();
		for(String path : config.getKeys(false)){
			NamedHologram hologram = NamedHologramManager.getHologram(path);
			long id = Long.parseLong(path);
			String[] data = config.getString(path).split(",");
			UUID owner = UUID.fromString(data[0]);
			Like like = new Like(hologram, owner, Integer.parseInt(data[1]));
			likes.put(id, like);
			if(!playerLikes.containsKey(owner)) playerLikes.put(owner, new ArrayList<>());
			playerLikes.get(owner).add(like);
		}
		return Tuple.of(likes, playerLikes);
	}
	
	public void remove(Like like){
		FileConfiguration config = config();
		config.set(String.valueOf(like.id), null);
		update();
	}
	
	public void writeAll(){
		FileConfiguration config = config();
		plugin.likes.forEach((id, like) -> config.set(String.valueOf(id), like.toString()));
		update();
	}

}
