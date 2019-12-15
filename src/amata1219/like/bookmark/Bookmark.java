package amata1219.like.bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import amata1219.like.Like;
import amata1219.like.bookmark.Order;

public class Bookmark {
	
	private final List<Like> likes;
	private Order order;
	
	public Bookmark(){
		likes = new ArrayList<>();
		order = Order.REGISTRATION_TIME_IN_DESCENDING;
	}
	
	public Bookmark(List<Like> likes, Order order){
		this.likes = likes;
		this.order = order;
	}
	
	public List<Like> likes(){
		return new ArrayList<>(likes);
	}
	
	public Order order(){
		return order;
	}
	
	public void setOrder(Order order){
		this.order = Objects.requireNonNull(order);
	}
	
}
