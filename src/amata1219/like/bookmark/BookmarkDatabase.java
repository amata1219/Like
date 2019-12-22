package amata1219.like.bookmark;


import java.util.ArrayList;
import java.util.HashMap;

import amata1219.like.Like;
import amata1219.like.config.Yaml;
import amata1219.like.exception.NotImplementedException;

public class BookmarkDatabase extends Yaml {

	public BookmarkDatabase() {
		super("bookmark_data.yml");
	}

	@Override
	public void readAll() {
		throw new NotImplementedException();
	}
	
	public HashMap<String, Bookmark> load(){
		HashMap<String, Bookmark> bookmarks = new HashMap<>();
		for(String path : getKeys(false)){
			String data = getString(path);
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
		plugin.bookmarks.remove(bookmark.name);
		set(bookmark.name, null);
		update();
	}
	
	public void save(){
		plugin.bookmarks.forEach((name, bookmark) -> set(name, bookmark.toString()));
		update();
	}

}
