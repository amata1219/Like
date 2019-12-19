package amata1219.like.slash.dsl.component;

import java.util.function.Supplier;

import amata1219.like.monad.Result;

public class LabeledStatement<T, F, S> {
	
	public final Matcher<T> matcher;
	private final Supplier<Result<F, S>> expression;
	
	public LabeledStatement(Matcher<T> matcher, Supplier<Result<F, S>> expression){
		this.matcher = matcher;
		this.expression = expression;
	}
	
	public Result<F, S> evaluate(){
		return expression.get();
	}

}
