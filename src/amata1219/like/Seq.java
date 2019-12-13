package amata1219.like;

import amata1219.like.monad.Option;

public class Seq<T> {
	
	private T head;
	private Seq<T> tail;
	
	public static <T> Seq<T> of(T value){
		return new Seq<>(value, null);
	}
	
	public Seq(T head, Seq<T> tail){
		this.head = head;
		this.tail = tail;
	}
	
	public Option<T> head(){
		return Option.of(head);
	}
	
	public Option<Seq<T>> tail(){
		return Option.of(tail);
	}
	
	public Option<T> apply(int index){
		if(index < 0) return Option.None();
		return index == 0 ? head() : tail().flatMap(s -> s.apply(index - 1));
	}
	
	public Option<Integer> indexOf(T value){
		return value == head ? Option.Some(0) : tail().flatMap(s -> s.indexOf(value)).map(i -> i + 1);
	}
	
	public Seq<T> add(T value){
		return new Seq<>(value, this);
	}
	
	public Seq<T> remove(T value){
		if(value == head) return tail;
		tail().then(s -> s.remove(value, this));
		return this;
	}
	
	private void remove(T value, Seq<T> parent){
		if(value == head) parent.tail = tail;
		else tail().then(s -> s.remove(value, this));
	}
	
}
