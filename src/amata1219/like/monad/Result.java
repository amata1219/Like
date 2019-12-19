package amata1219.like.monad;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import amata1219.like.slash.dsl.component.LabeledStatement;

public interface Result<F, S> {
	
	public static <S> Result<String, S> unit(S value){
		return Success(value);
	}
	
	public static Result<String, ?> error(String error){
		return new Failure<>(error);
	}
	
	public static <F, S> Result<F, S> Success(S value){
		return new Success<>(value);
	}
	
	public static <F, S> Result<F, S> Failure(F error){
		return new Failure<>(error);
	}
	
	<T> Result<F, T> flatMap(Function<S, Result<F, T>> mapper);
	
	@SuppressWarnings("unchecked")
	default <T> Result<F, T> map(Function<S, T> mapper){
		return (Result<F, T>) flatMap(mapper.andThen(Result::Success));
	}
	
	Result<F, S> filter(Predicate<S> predicate, Supplier<F> error);
	
	default Result<F, S> filterNot(Predicate<S> predicate, Supplier<F> error){
		return filter(predicate.negate(), error);
	}
	
	Result<F, ?> match(@SuppressWarnings("unchecked") LabeledStatement<S, F, ?>... statements);
	
	Result<F, S> onSuccess(Consumer<S> action);
	
	Result<F, S> onFailure(Consumer<F> action);
	
	public class Success<F, S> implements Result<F, S> {
		
		private final S value;
		
		private Success(S value){
			this.value = value;
		}

		@Override
		public <T> Result<F, T> flatMap(Function<S, Result<F, T>> mapper) {
			return mapper.apply(value);
		}
		
		@Override
		public Result<F, S> filter(Predicate<S> predicate, Supplier<F> error) {
			return predicate.test(value) ? this : Failure(error.get());
		}

		@Override
		public Result<F, ?> match(@SuppressWarnings("unchecked") LabeledStatement<S, F, ?>... statements) {
			for(LabeledStatement<S, F, ?> statement : statements) if(statement.matcher.match(value)){
				Result<F, ?> result = statement.evaluate();
				return result != null ? result : this;
			}
			return this;
		}

		@Override
		public Result<F, S> onSuccess(Consumer<S> action) {
			action.accept(value);
			return this;
		}

		@Override
		public Result<F, S> onFailure(Consumer<F> action) {
			return this;
		}

	}
	
	public class Failure<F, S> implements Result<F, S> {
		
		private final F error;
		
		private Failure(F error){
			this.error = error;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> Result<F, T> flatMap(Function<S, Result<F, T>> mapper) {
			return (Result<F, T>) this;
		}
		
		@Override
		public Result<F, S> filter(Predicate<S> predicate, Supplier<F> error) {
			return this;
		}

		@Override
		public Result<F, ?> match(@SuppressWarnings("unchecked") LabeledStatement<S, F, ?>... statements) {
			return this;
		}

		@Override
		public Result<F, S> onSuccess(Consumer<S> action) {
			return this;
		}

		@Override
		public Result<F, S> onFailure(Consumer<F> action) {
			action.accept(error);
			return this;
		}

	}

}
