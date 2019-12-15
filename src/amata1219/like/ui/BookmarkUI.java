package amata1219.like.ui;

import java.util.List;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.bookmark.Bookmark;
import amata1219.masquerade.dsl.component.Layout;

public class BookmarkUI extends AbstractMultipleUI {
	
	private final Bookmark bookmark;
	
	public BookmarkUI(Bookmark bookmark){
		this.bookmark = bookmark;
	}

	@Override
	protected List<Like> likes() {
		return bookmark.likes();
	}

	@Override
	protected void layout(Player p, Layout l, List<Like> likes) {
	}

}
