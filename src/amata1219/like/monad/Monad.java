package amata1219.like.monad;

import java.util.function.Function;

import org.bukkit.util.Consumer;

public interface Monad<T> {
	
	<U> Monad<U> map(Function<T, U> mapper);
	
	void consume(Consumer<T> action);
	
	default Monad<T> then(Consumer<T> action){
		consume(action);
		return this;
	}

}
