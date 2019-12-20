package amata1219.like.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.Yaml;
import amata1219.like.exception.NotImplementedException;

public class PlayerDataLoading extends Yaml {
	
	public PlayerDataLoading(){
		super(Main.plugin(), "player_data.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}
	
	public HashMap<UUID, PlayerData> load(HashMap<UUID, List<Like>> playerLikes){
		HashMap<UUID, PlayerData> players = new HashMap<>();
		for(String path : getKeys(false)){
			PlayerData data = new PlayerData();
			UUID uuid = UUID.fromString(path);
			playerLikes.getOrDefault(uuid, Collections.emptyList()).forEach(data::addLike);
			Arrays.stream(getString(uuid.toString()).split(","))
				.map(Long::valueOf)
				.map(plugin.likes::get)
				.forEach(data::addFavoriteLike);
			players.put(uuid, data);
		}
		return players;
	}
	
	public void save(UUID uuid, PlayerData data){
		String text = data.favoriteLikes.values().stream()
		.map(l -> l.id)
		.map(String::valueOf)
		.collect(Collectors.joining(","));
		set(uuid.toString(), text);
		update();
	}

}
