package amata1219.like.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class Reflector {

	public final static String VERSION = Bukkit.getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1");
	public final static String NMS_PACKAGE_NAME = "net.minecraft.server.v" + VERSION;
	public final static String OBC_PACKAGE_NAME = "org.bukkit.craftbukkit.v" + VERSION;

	public static Class<?> clazz(String className){
		Class<?> clazz = null;
		try{
			clazz = Class.forName(className);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return clazz;
	}

	public static Class<?> nmsClass(String className){
		return clazz(NMS_PACKAGE_NAME + "." + className);
	}

	public static Class<?> obcClass(String className){
		return clazz(OBC_PACKAGE_NAME + "." + className);
	}

	public static Constructor<?> constructor(Class<?> clazz, Class<?>... parameterTypes){
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return constructor;
	}

	public static Object newInstance(Constructor<?> constructor, Object... initargs){
		Object instance = null;
		try {
			instance = constructor.newInstance(initargs);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static Method method(Class<?> clazz, String methodName, Class<?>... parameterTypes){
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object instance, Object... parameters){
		T value = null;
		try {
			value = (T) method.invoke(instance, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static Field field(Class<?> clazz, String fieldName){
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fieldValue(Field field, Object instance){
		T value = null;
		try {
			value = (T) field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static void setFieldValue(Field field, Object instance, Object value){
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
