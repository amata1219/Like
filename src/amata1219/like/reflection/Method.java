package amata1219.like.reflection;

import java.lang.reflect.InvocationTargetException;

import amata1219.like.monad.Maybe;

public class Method<T, R> {
	
	public static <T, R> Method<T, R> of(Class<T> clazz, String name, Class<?>... parameterTypes){
		try {
			return new Method<>(clazz.getDeclaredMethod(name, parameterTypes));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> Method<T, Void> of_(Class<T> clazz, String name, Class<?>... parameterTypes){
		return of(clazz, name, parameterTypes);
	}
	
	private final java.lang.reflect.Method method;
	
	private Method(java.lang.reflect.Method method){
		this.method = method;
	}
	
	@SuppressWarnings("unchecked")
	public Maybe<R> invoke(T instance, Object... args){
		try {
			return Maybe.unit((R) method.invoke(instance, args));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return Maybe.None();
	}

}
