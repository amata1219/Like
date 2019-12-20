package amata1219.like.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.exception.NotImplementedException;
import amata1219.like.tuplet.Tuple;

public class LikeDatabase extends Yaml {

	public LikeDatabase() {
		super(Main.plugin(), "like_data.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}
	
	public Tuple<HashMap<Long, Like>, HashMap<UUID, List<Like>>> load(){
		HashMap<Long, Like> likes = new HashMap<>();
		HashMap<UUID, List<Like>> playerLikes = new HashMap<>();
		for(String path : getKeys(false)){
			NamedHologram hologram = NamedHologramManager.getHologram(path);
			long id = Long.parseLong(path);
			String[] data = getString(path).split(",");
			UUID owner = UUID.fromString(data[0]);
			Like like = new Like(hologram, owner, Integer.parseInt(data[1]));
			likes.put(id, like);
			if(!playerLikes.containsKey(owner)) playerLikes.put(owner, new ArrayList<>());
			playerLikes.get(owner).add(like);
		}
		return Tuple.of(likes, playerLikes);
	}
	
	public void remove(Like like){
		set(String.valueOf(like.id), null);
		update();
	}
	
	public void save(){
		plugin.likes.forEach((id, like) -> set(String.valueOf(id), like.toString()));
		update();
	}

}
