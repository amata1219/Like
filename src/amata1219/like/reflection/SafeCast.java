package amata1219.like.reflection;

import amata1219.like.monad.Maybe;

public class SafeCast {
	
	@SuppressWarnings("unchecked")
	public static <T> Maybe<T> cast(Object value, Class<T> clazz){
		return clazz.isInstance(value) ? Maybe.Some((T) value) : Maybe.None();
	}

}
