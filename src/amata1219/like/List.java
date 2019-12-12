package amata1219.like;

import static amata1219.like.monad.Option.*;

import amata1219.like.monad.Option;

public class List<T> {
	
	private final T head;
	private List<T> tail;
	
	public List(T head, List<T> tail){
		this.head = head;
		this.tail = tail;
	}
	
	public List(T head){
		this(head, null);
	}
	
	public Option<T> head(){
		return of(head);
	}
	
	public Option<List<T>> tail(){
		return of(tail);
	}
	
	public int size(){
		return 1 + tail().map(List::size).or(() -> 0);
	}
	
	public Option<Integer> indexOf(T value){
		return value == head ? Some(0) : tail().flatMap(l -> l.indexOf(value)).map(i -> i + 1);
	}
	
	public boolean contains(T value){
		return indexOf(value).isSome();
	}
	
	public Option<T> apply(int index){
		if(index < 0) return None();
		else if(index == 0) return head();
		else return tail().flatMap(l -> l.apply(index - 1));
	}
	
	public List<T> add(T value){
		return new List<>(value, this);
	}
	
	public List<T> remove(T value){
		return remove(value, null).or(() -> this);
	}
	
	private Option<List<T>> remove(T value, List<T> parent){
		if(value != head) return tail().flatMap(l -> l.remove(value, this));
		if(parent == null) return Some(new List<>(null, null));
		if(tail == null) return None();
		parent.tail = tail;
		return Some(parent);
	}
	
}
