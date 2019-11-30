package amata1219.like.bookmark;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import amata1219.like.OldLike;

public class Bookmark {
	
	private final List<OldLike> likes;
	
	public Bookmark(Collection<OldLike> likes){
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
