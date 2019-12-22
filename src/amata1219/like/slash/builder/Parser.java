package amata1219.like.slash.builder;

import static amata1219.like.monad.Either.*;

import java.util.function.Function;

import amata1219.like.slash.effect.MessageEffect;
import amata1219.like.monad.Either;

public interface Parser<T>{
	
	static Parser<String> identity(){
		return x -> Success(x);
	}
	
	static Parser<Boolean> bool(MessageEffect error){
		return convert(error, Boolean::valueOf);
	}
	
	static Parser<Integer> i32(MessageEffect error){
		return convert(error, Integer::valueOf);
	}
	
	static Parser<Long> i64(MessageEffect error){
		return convert(error, Long::valueOf);
	}
	
	static Parser<Integer> u32(MessageEffect error){
		return convert(error, x -> {
			int i = Integer.parseInt(x);
			if(i < 0) throw new IllegalArgumentException();
			return i;
		});
	}
	
	static Parser<Long> u64(MessageEffect error){
		return convert(error, x -> {
			long i = Long.parseLong(x);
			if(i < 0) throw new IllegalArgumentException();
			return i;
		});
	}
	
	static Parser<Float> f32(MessageEffect error){
		return convert(error, Float::valueOf);
	}
	
	static Parser<Double> f64(MessageEffect error){
		return convert(error, Double::valueOf);
	}
	
	static <T> Parser<T> convert(MessageEffect error, Function<String, T> converter){
		return x -> {
			try{
				return Success(converter.apply(x));
			}catch(Exception e){
				return Failure(error);
			}
		};
	}
	
	Either<MessageEffect, T> parse(String arg);

}
