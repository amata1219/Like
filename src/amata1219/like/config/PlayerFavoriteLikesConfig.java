package amata1219.like.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.chunk.LikeMap;
import amata1219.like.exception.NotImplementedException;

public class PlayerFavoriteLikesConfig extends Yaml {
	
	public PlayerFavoriteLikesConfig(){
		super(Main.instance(), "player_data.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}
	
	public Collection<Like> favoriteLikes(UUID uuid){
		String data = getString(uuid.toString());
		return Arrays.stream(data.split(","))
		.map(Long::valueOf)
		.map(Main.instance().likes::get)
		.collect(Collectors.toList());
	}
	
	public void save(UUID uuid, LikeMap favoriteLikes){
		set(uuid.toString(), favoriteLikes.toString());
		update();
	}

}
