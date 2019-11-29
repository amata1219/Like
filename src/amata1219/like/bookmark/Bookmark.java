package amata1219.like.bookmark;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import amata1219.like.Like;

public class Bookmark {
	
	private final List<Like> likes;
	
	public Bookmark(Collection<Like> likes){
		this.likes = likes.stream()
				.sorted((l1, l2) -> l1.getLikeCount() - l2.getLikeCount())
				.collect(Collectors.toList());
	}
	
	/*
	 * add
	 * remove
	 * sort(onUpdateLikeCount)
	 */

}
