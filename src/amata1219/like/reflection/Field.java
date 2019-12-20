package amata1219.like.reflection;

import amata1219.like.monad.Maybe;

public class Field<T, U> {
	
	public static <T, U> Field<T, U> of(Class<T> clazz, String name){
		try {
			return new Field<>(clazz.getDeclaredField(name));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T, U>Field<T, U> of(Class<T> clazz, Class<U> type, String name){
		return of(clazz, name);
	}
	
	private final java.lang.reflect.Field field;
	
	private Field(java.lang.reflect.Field field){
		this.field = field;
		this.field.setAccessible(true);
	}
	
	@SuppressWarnings("unchecked")
	public Maybe<U> get(T instance){
		try {
			return Maybe.unit((U) field.get(instance));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return Maybe.None();
	}
	
	public void set(T instance, U value){
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
