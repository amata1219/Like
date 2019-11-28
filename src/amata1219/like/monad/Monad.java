package amata1219.like.monad;

import java.util.function.Function;

import org.bukkit.util.Consumer;

public interface Monad<T> {
	
	<S> Monad<S> map(Function<T, S> mapper);
	
	Monad<T> then(Consumer<T> action);
	
}
