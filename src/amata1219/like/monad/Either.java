package amata1219.like.monad;

import java.util.function.Function;

import org.bukkit.util.Consumer;

public abstract class Either<R> implements Monad<R> {
	
	public static <R> Either<R> Right(R result){
		return new Right<>(result);
	}
	
	public static <R> Either<R> Left(String error){
		return new Left<>(error);
	}
	
	@Override
	public abstract <U> Either<U> map(Function<R, U> mapper);
	
	public abstract <U> Either<U> flatMap(Function<R, Either<U>> mapper);
	
	@Override
	public abstract Either<R> then(Consumer<R> action);
	
	public abstract Either<R> onFailure(Consumer<String> action);
	
	public static class Left<R> extends Either<R> {
		
		private final String error;
		
		private Left(String error){
			this.error = error;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> Either<U> map(Function<R, U> mapper) {
			return (Either<U>) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> Either<U> flatMap(Function<R, Either<U>> mapper) {
			return (Either<U>) this;
		}

		@Override
		public Either<R> then(Consumer<R> action) {
			return this;
		}

		@Override
		public Either<R> onFailure(Consumer<String> action) {
			action.accept(error);
			return this;
		}
		
	}
	
	public static class Right<R> extends Either<R> {
		
		private final R result;
		
		private Right(R result){
			this.result = result;
		}

		@Override
		public <U> Either<U> map(Function<R, U> mapper) {
			return Right(mapper.apply(result));
		}

		@Override
		public <U> Either<U> flatMap(Function<R, Either<U>> mapper) {
			return mapper.apply(result);
		}

		@Override
		public Either<R> then(Consumer<R> action) {
			action.accept(result);
			return this;
		}

		@Override
		public Either<R> onFailure(Consumer<String> action) {
			return this;
		}
		
	}
	

}
