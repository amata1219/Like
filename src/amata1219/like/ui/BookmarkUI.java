package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.entity.Player;

import amata1219.like.bookmark.Bookmark;
import amata1219.masquerade.dsl.component.Layout;

public class BookmarkUI extends AbstractMultipleUI {
	
	private final Bookmark bookmark;
	
	public BookmarkUI(Bookmark bookmark){
		this.bookmark = bookmark;
	}

	@Override
	public Function<Player, Layout> layout() {
		return null;
	}

}
