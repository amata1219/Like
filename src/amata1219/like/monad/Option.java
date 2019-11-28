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
	
	@Override
	public abstract <S> Option<S> map(Function<T, S> mapper);
	
	public abstract <S> Option<S> flatMap(Function<T, Option<S>> mapper);
	
	@Override
	public abstract Option<T> then(Consumer<T> action);
	
	public static class Some<T> extends Option<T> {
		
		private final T value;
		
		private Some(T value){
			this.value = Objects.requireNonNull(value);
		}
		
		@Override
		public <S> Option<S> map(Function<T, S> mapper) {
			return Some(mapper.apply(value));
		}

		public <S> Option<S> flatMap(Function<T, Option<S>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public Option<T> then(Consumer<T> action) {
			action.accept(value);
			return this;
		}
		
	}
	
	public static class None<T> extends Option<T> {
		
		static final None<?> INSTANCE = new None<>();
		
		private None(){
			
		}

		@Override
		public <S> Option<S> map(Function<T, S> mapper) {
			return None();
		}

		@Override
		public <S> Option<S> flatMap(Function<T, Option<S>> mapper) {
			return None();
		}

		@Override
		public Option<T> then(Consumer<T> action) {
			return None();
		}
		
	}

}
