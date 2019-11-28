package amata1219.like.monad;

import java.util.Objects;
import java.util.function.Function;

import org.bukkit.util.Consumer;

public abstract class Try<T> implements Monad<T> {
	
	public static <T> Try<T> Success(T value){
		return new Success<>(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Try<T> Failure(){
		return (Try<T>) Failure.INSTANCE;
	}
	
	public static <T> Try<T> of(T value){
		return value != null ? Success(value) : Failure();
	}
	
	@Override
	public abstract <S> Try<S> map(Function<T, S> mapper);
	
	public abstract <S> Try<S> flatMap(Function<T, Try<S>> mapper);
	
	@Override
	public abstract Try<T> then(Consumer<T> action);
	
	public static class Success<T> extends Try<T> {
		
		private final T value;
		
		private Success(T value){
			this.value = Objects.requireNonNull(value);
		}

		@Override
		public <U> Try<U> map(Function<T, U> mapper) {
			try{
				return Success(mapper.apply(value));
			}catch(Throwable t){
				return Failure();
			}
		}

		@Override
		public <S> Try<S> flatMap(Function<T, Try<S>> mapper) {
			try{
				return mapper.apply(value);
			}catch(Throwable t){
				return Failure();
			}
		}
		
		
		@Override
		public Try<T> then(Consumer<T> action){
			try{
				action.accept(value);
				return this;
			}catch(Throwable t){
				return Failure();
			}
		}
		
	}
	
	public static class Failure<T> extends Try<T> {
		
		private final static Failure<?> INSTANCE = new Failure<>();
		
		private Failure(){
			
		}

		@Override
		public <U> Try<U> map(Function<T, U> mapper) {
			return Failure();
		}

		@Override
		public <S> Try<S> flatMap(Function<T, Try<S>> mapper) {
			return Failure();
		}
		
		@Override
		public Try<T> then(Consumer<T> action) {
			return Failure();
		}
		
	}
	
}
