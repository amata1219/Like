package amata1219.like.monad;

import java.util.Objects;
import java.util.function.Function;

import org.bukkit.util.Consumer;

public abstract class Option<T> implements Monad<T> {
	
	public static <T> Some<T> Some(T value){
		return new Some<>(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> None<T> None(){
		return (None<T>) None.INSTANCE;
	}
	
	public static <T> Option<T> of(T value){
		return value != null ? Some(value) : None();
	}
	
	public abstract <U> Option<U> flatMap(Function<T, Option<U>> mapper);
	
	public static class Some<T> extends Option<T> {
		
		private final T value;
		
		private Some(T value){
			this.value = Objects.requireNonNull(value);
		}
		
		@Override
		public <U> Option<U> map(Function<T, U> mapper) {
			return Some(mapper.apply(value));
		}

		public <U> Option<U> flatMap(Function<T, Option<U>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public void consume(Consumer<T> action) {
			action.accept(value);
		}
		
	}
	
	public static class None<T> extends Option<T> {
		
		static final None<?> INSTANCE = new None<>();
		
		private None(){
			
		}

		@Override
		public <U> Option<U> map(Function<T, U> mapper) {
			return None();
		}

		@Override
		public <U> Option<U> flatMap(Function<T, Option<U>> mapper) {
			return None();
		}

		@Override
		public void consume(Consumer<T> action) {
			
		}
		
	}

}
