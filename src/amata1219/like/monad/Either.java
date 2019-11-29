package amata1219.like.monad;

import java.util.function.Function;

import org.bukkit.util.Consumer;

public abstract class Either<L, R> implements Monad<R> {
	
	public static <L, R> Either<L, R> Right(R result){
		return new Right<>(result);
	}
	
	public static <L, R> Either<L, R> Left(L error){
		return new Left<>(error);
	}
	
	@Override
	public abstract <U> Either<L, U> map(Function<R, U> mapper);
	
	public abstract <U> Either<L, U> flatMap(Function<R, Either<L, U>> mapper);
	
	@Override
	public abstract Either<L, R> then(Consumer<R> action);
	
	public abstract Either<L, R> onFailure(Consumer<L> action);
	
	public static class Left<L, R> extends Either<L, R> {
		
		private final L error;
		
		private Left(L error){
			this.error = error;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> Either<L, U> map(Function<R, U> mapper) {
			return (Either<L, U>) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> Either<L, U> flatMap(Function<R, Either<L, U>> mapper) {
			return (Either<L, U>) this;
		}

		@Override
		public Either<L, R> then(Consumer<R> action) {
			return this;
		}

		@Override
		public Either<L, R> onFailure(Consumer<L> action) {
			action.accept(error);
			return this;
		}
		
	}
	
	public static class Right<L, R> extends Either<L, R> {
		
		private final R result;
		
		private Right(R result){
			this.result = result;
		}

		@Override
		public <U> Either<L, U> map(Function<R, U> mapper) {
			return Right(mapper.apply(result));
		}

		@Override
		public <U> Either<L, U> flatMap(Function<R, Either<L, U>> mapper) {
			return mapper.apply(result);
		}

		@Override
		public Either<L, R> then(Consumer<R> action) {
			action.accept(result);
			return this;
		}

		@Override
		public Either<L, R> onFailure(Consumer<L> action) {
			return this;
		}
		
	}
	

}
