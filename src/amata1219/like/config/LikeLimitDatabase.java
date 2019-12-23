package amata1219.like.config;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import amata1219.like.exception.NotImplementedException;

public class LikeLimitDatabase extends Config {

	public LikeLimitDatabase() {
		super("like_limit.yml");
	}

	@Override
	public void load() {
		throw new NotImplementedException();
	}
	
	public int limit(UUID uuid){
		FileConfiguration config = config();
		String path = uuid.toString();
		if(!config.contains(path)) config.set(path, config.getInt("Default"));
		return config.getInt(path);
	}
	
	public void set(UUID uuid, int limit){
		FileConfiguration config = config();
		config.set(uuid.toString(), limit);
		update();
	}

}
