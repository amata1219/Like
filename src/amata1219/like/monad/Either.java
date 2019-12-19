package amata1219.like.monad;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Either<F, S> {
	
	public static <S> Either<String, S> unit(S value){
		return Success(value);
	}
	
	public static Either<String, ?> error(String error){
		return Failure(error);
	}
	
	public static <F, S> Either<F, S> Success(S value){
		return new Success<>(value);
	}
	
	public static <F, S> Either<F, S> Failure(F error){
		return new Failure<>(error);
	}
	
	<T> Either<F, T> flatMap(Function<S, Either<F, T>> mapper);
	
	@SuppressWarnings("unchecked")
	default <T> Either<F, T> map(Function<S, T> mapper){
		return (Either<F, T>) flatMap(mapper.andThen(Either::Success));
	}
	
	Either<F, S> onSuccess(Consumer<S> action);
	
	Either<F, S> onFailure(Consumer<F> action);
	
	public class Success<F, S> implements Either<F, S> {
		
		private final S value;
		
		private Success(S value){
			this.value = value;
		}

		@Override
		public <T> Either<F, T> flatMap(Function<S, Either<F, T>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public Either<F, S> onSuccess(Consumer<S> action) {
			action.accept(value);
			return this;
		}

		@Override
		public Either<F, S> onFailure(Consumer<F> action) {
			return this;
		}
		
	}
	
	public class Failure<F, S> implements Either<F, S> {
		
		private final F error;
		
		private Failure(F error){
			this.error = error;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Either<F, T> flatMap(Function<S, Either<F, T>> mapper) {
			return (Either<F, T>) this;
		}

		@Override
		public Either<F, S> onSuccess(Consumer<S> action) {
			return this;
		}

		@Override
		public Either<F, S> onFailure(Consumer<F> action) {
			action.accept(error);
			return this;
		}
		
	}

}
