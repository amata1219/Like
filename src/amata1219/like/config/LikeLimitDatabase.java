package amata1219.like.config;

import java.util.UUID;

import amata1219.like.Main;
import amata1219.like.exception.NotImplementedException;

public class LikeLimitDatabase extends Yaml {

	public LikeLimitDatabase() {
		super(Main.plugin(), "like_limit.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}
	
	public int limit(UUID uuid){
		String path = uuid.toString();
		if(!contains(path)) set(path, getInt("Default"));
		return getInt(path);
	}
	
	public void set(UUID uuid, int limit){
		set(uuid.toString(), limit);
	}

}
