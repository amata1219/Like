package amata1219.like.config;

import amata1219.like.Main;
import amata1219.like.exception.NotImplementedException;

public class LikeLoading extends Yaml {

	public LikeLoading() {
		super(Main.plugin(), "like_data.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}

}
