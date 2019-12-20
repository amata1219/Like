package amata1219.like.slash.dsl.component;

import java.util.function.Predicate;
import java.util.function.Supplier;

import amata1219.like.monad.Result;

public abstract class Matcher<T> {
	
	private final static Default<?> DEFAULT = new Default<>();
	
	@SafeVarargs
	public static <T> Matcher<T> Case(T... literals){
		return new Literal<>(literals);
	}
	
	public static <T, F, S> LabeledStatement<T, F, S> Case(Predicate<T> predicate, Supplier<Result<F, S>> expression){
		return new LabeledStatement<>(new Condition<>(predicate), expression);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, F, S> LabeledStatement<T, F, S> Else(Supplier<Result<F, S>> expression){
		return (LabeledStatement<T, F, S>) DEFAULT.label(expression);
	}
	
	public static <T, F, S> LabeledStatement<T, F, S> E1se(Supplier<F> expression){
		return Else(() -> Result.Failure(expression.get()));
	}
	
	public static <F, S> Result<F, S> None(){
		return null;
	}
	
	public static <F, S> Result<F, S> Message(F message){
		return Result.Failure(message);
	}
	
	public <F, S> LabeledStatement<T, F, S> label(Supplier<Result<F, S>> expression){
		return new LabeledStatement<>(this, expression);
	}
	
	public <F, S> LabeledStatement<T, F, S> then(Runnable action){
		action.run();
		return label(() -> null);
	}
	
	public abstract boolean match(T value);
	
	private static class Literal<T> extends Matcher<T> {
		
		private final T[] literals;
		
		@SafeVarargs
		private Literal(T... literals){
			this.literals = literals;
		}
		
		@Override
		public boolean match(T value){
			for(T literal : literals) if(literal == value) return true;
			return false;
		}
		
	}
	
	private static class Condition<T> extends Matcher<T> {
		
		private final Predicate<T> predicate;
		
		private Condition(Predicate<T> predicate){
			this.predicate = predicate;
		}
		
		@Override
		public boolean match(T value){
			return predicate.test(value);
		}
		
	}
	
	private static class Default<T> extends Matcher<T> {

		private Default(){
			
		}
		
		@Override
		public boolean match(T value) {
			return true;
		}
		
	}

}
