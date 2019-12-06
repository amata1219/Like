package amata1219.like.config;

import java.util.Arrays;
import java.util.UUID;

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
	
	public LikeMap favoriteLikes(UUID uuid){
		LikeMap map = new LikeMap();
		String data = getString(uuid.toString());
		Arrays.stream(data.split(","))
		.mapToLong(Long::parseLong)
		.mapToObj(Main.instance()::like)
		.forEach(o -> o.then(map::put));
		return map;
	}
	
	public void save(UUID uuid, LikeMap favoriteLikes){
		set(uuid.toString(), favoriteLikes.toString());
		update();
	}

}
