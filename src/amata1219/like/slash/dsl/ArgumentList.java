package amata1219.like.slash.dsl;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Joiner;

import amata1219.like.monad.Result;

public class ArgumentList<F> {

	private final Queue<String> args;

	public ArgumentList(String[] args){
		this.args = new LinkedList<>(Arrays.asList(args));
	}
	
	public boolean isEmpty(){
		return args.isEmpty();
	}

	public int length(){
		return args.size();
	}
	
	public <R> Result<F, R> next(Function<String, R> converter, Supplier<F> error){
		try{
			return Result.Success(converter.apply(args.poll()));
		}catch(Exception e){
			return Result.Failure(error.get());
		}
	}
	
	public Result<F, String> next(Supplier<F> error){
		return next(Function.identity(), error);
	}
	
	public Result<F, String> nextOr(Supplier<String> other){
		if(isEmpty()) args.add(other.get());
		return next(null);
	}

	public Result<F, Boolean> nextBoolean(Supplier<F> error){
		return next(Boolean::valueOf, error);
	}

	public Result<F, Character> nextChar(Supplier<F> error){
		return next(s -> s.length() == 1 ? s.charAt(0) : null, error);
	}

	public Result<F, Byte> nextByte(Supplier<F> error){
		return next(Byte::valueOf, error);
	}

	public Result<F, Short> nextShort(Supplier<F> error){
		return next(Short::valueOf, error);
	}

	public Result<F, Integer> nextInt(Supplier<F> error){
		return next(Integer::valueOf, error);
	}

	public Result<F, Long> nextLong(Supplier<F> error){
		return next(Long::valueOf, error);
	}

	public Result<F, Float> nextFloat(Supplier<F> error){
		return next(Float::valueOf, error);
	}

	public Result<F, Double> nextDouble(Supplier<F> error){
		return next(Double::valueOf, error);
	}

	public <T> Result<F, T> range(int count, Function<Collection<String>, Result<F, T>> action){
		Collection<String> ranged = IntStream.range(0, count)
				.mapToObj(i -> args.poll())
				.collect(Collectors.toList());
		return action.apply(ranged);
	}
	
	public Result<F, String> join(int count,  Supplier<F> error){
		return range(count, ranged -> ranged.isEmpty() ? Result.Failure(error.get()) : Result.Success(Joiner.on(' ').join(ranged)));
	}
	
	public ArgumentList<F> skip(int count){
		for(int i = Math.min(count, length()); i > 0; i--) args.remove();
		return this;
	}

}
