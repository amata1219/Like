package amata1219.like.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.chunk.LikeMap;
import amata1219.like.exception.NotImplementedException;
import amata1219.like.player.PlayerData;

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
				.map(Main.plugin().likes::get)
				.forEach(data::addFavoriteLike);
			players.put(uuid, data);
		}
		return players;
	}
	
	public PlayerData loadExistingPlayerData(UUID uuid){
		PlayerData data = new PlayerData();
	}
	
	public Collection<Like> favoriteLikes(UUID uuid){
		return Arrays.stream(getString(uuid.toString()).split(","))
				.map(Long::valueOf)
				.map(Main.plugin().likes::get)
				.collect(Collectors.toList());
	}
	
	public void save(UUID uuid, LikeMap favoriteLikes){
		set(uuid.toString(), favoriteLikes.toString());
		update();
	}

}
