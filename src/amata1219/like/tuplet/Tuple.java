package amata1219.like.tuplet;

public class Tuple<F, S> {
	
	public final F first;
	public final S second;
	
	public static <F, S> Tuple<F, S> of(F first, S second){
		return new Tuple<>(first, second);
	}
	
	private Tuple(F first, S second){
		this.first = first;
		this.second = second;
	}
}
