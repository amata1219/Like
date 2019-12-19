package amata1219.like.config;

import java.util.Arrays;
import java.util.Collection;
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
	
	public HashMap<UUID, PlayerData> loadExistingAllPlayerData(){
	}
	
	public PlayerData loadExistingPlayerData(UUID uuid){
		
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
