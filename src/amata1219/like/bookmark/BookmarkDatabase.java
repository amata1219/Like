package amata1219.like.bookmark;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

import amata1219.like.Like;
import amata1219.like.config.Config;

public class BookmarkDatabase extends Config {

	public BookmarkDatabase() {
		super("bookmark_data.yml");
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException();
	}
	
	public HashMap<String, Bookmark> readAll(){
		FileConfiguration config = config();
		HashMap<String, Bookmark> bookmarks = new HashMap<>();
		for (String path : config.getKeys(false)) {
			String data = config.getString(path);
			String[] parts = data.split(":");
			Order order = Order.values()[Integer.parseInt(parts[0])];
			ArrayList<Like> likes = new ArrayList<>();
			if(parts.length > 1){
				for(String likedata : parts[1].split(",")){
					Long id = Long.valueOf(likedata);
					Like like = plugin.likes.get(id);
					if(like != null) likes.add(like);
				}
			}
			Bookmark bookmark = new Bookmark(path, likes, order);
			bookmarks.put(path, bookmark);
		}
		return bookmarks;
	}
	
	public void remove(Bookmark bookmark){
		FileConfiguration config = config();
		plugin.bookmarks.remove(bookmark.name);
		config.set(bookmark.name, null);
		update();
	}
	
	public void writeAll(){
		FileConfiguration config = config();
		plugin.bookmarks.forEach((name, bookmark) -> config.set(name, bookmark.toString()));
		update();
	}

}
