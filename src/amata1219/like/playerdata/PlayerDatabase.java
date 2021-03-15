package amata1219.like.playerdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;

import amata1219.like.Like;
import amata1219.like.config.Config;

public class PlayerDatabase extends Config {
	
	public PlayerDatabase(){
		super("player_data.yml");
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException();
	}
	
	public HashMap<UUID, PlayerData> readAll(HashMap<UUID, List<Like>> playerLikes){
		FileConfiguration config = config();
		HashMap<UUID, PlayerData> players = new HashMap<>();
		for(String path : config.getKeys(false)){
			PlayerData data = new PlayerData();
			UUID uuid = UUID.fromString(path);
			playerLikes.getOrDefault(uuid, Collections.emptyList()).forEach(data::registerLike);
			String[] likes = config.getString(uuid.toString()).split(",");
			if(!(likes.length == 1 && likes[0].isEmpty())){
				Arrays.stream(likes)
				.map(Long::valueOf)
				.map(plugin.likes::get)
				.forEach(data::favoriteLike);
			}
			players.put(uuid, data);
		}
		return players;
	}
	
	public void writeAll(){
		FileConfiguration config = config();
		plugin.players.forEach((uuid, data) -> {
			String text = data.favoriteLikes.values().stream()
					.map(l -> l.id)
					.map(String::valueOf)
					.collect(Collectors.joining(","));
			config.set(uuid.toString(), text);
		});
		update();
	}

}
