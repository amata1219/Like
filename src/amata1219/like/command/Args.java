package amata1219.like.command;

import java.util.function.Consumer;

public class Args {

	private final String[] args;
	private int index;

	public Args(String[] args){
		this.args = args;
	}

	public int length(){
		return args.length;
	}

	public String get(int index){
		return index < args.length ? args[index] : "";
	}

	public String get(int start, int end){
		String text = get(start++);
		while(start < end)
			text += " " + get(start++);
		return text;
	}

	public boolean hasNext(Type type){
		if(type == null || !hasNext())
			return false;

		try{
			type.getChecker().accept(args[index]);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public boolean hasNext(){
		return index < args.length;
	}

	public String next(){
		return hasNext() ? args[index++] : "";
	}

	public boolean hasNextBoolean(){
		return hasNext(Type.BOOLEAN);
	}

	public boolean nextBoolean(){
		return Boolean.parseBoolean(next());
	}

	public boolean hasNextChar(){
		return hasNext(Type.CHAR);
	}

	public char nextChar(){
		return hasNext() ? next().charAt(0) : ' ';
	}

	public boolean hasNextByte(){
		return hasNext(Type.BYTE);
	}

	public byte nextByte(){
		return hasNext() ? Byte.parseByte(next()) : -1;
	}

	public boolean hasNextShort(){
		return hasNext(Type.SHORT);
	}

	public short nextShort(){
		return hasNext() ? Short.parseShort(next()) : -1;
	}

	public boolean hasNextInt(){
		return hasNext(Type.INT);
	}

	public int nextInt(){
		return hasNext() ? Integer.parseInt(next()) : -1;
	}

	public boolean hasNextLong(){
		return hasNext(Type.LONG);
	}

	public long nextLong(){
		return hasNext() ? Long.parseLong(next()) : -1;
	}

	public boolean hasNextFloat(){
		return hasNext(Type.FLOAT);
	}

	public float nextFloat(){
		return hasNext() ? Float.parseFloat(next()) : -1;
	}

	public boolean hasNextDouble(){
		return hasNext(Type.DOUBLE);
	}

	public double nextDouble(){
		return hasNext() ? Double.parseDouble(next()) : -1;
	}

	public enum Type {

		STRING(text -> {}),
		BOOLEAN(text -> Boolean.parseBoolean(text)),
		CHAR(text -> {
			if(text.length() > 1)
				throw new IllegalArgumentException("Text length must be 1.");
			}),
		BYTE(text -> Byte.parseByte(text)),
		SHORT(text -> Short.parseShort(text)),
		INT(text -> Integer.parseInt(text)),
		LONG(text -> Long.parseLong(text)),
		FLOAT(text -> Float.parseFloat(text)),
		DOUBLE(text -> Double.parseDouble(text));

		private final Consumer<String> consumer;

		private Type(Consumer<String> consumer){
			this.consumer = consumer;
		}

		public Consumer<String> getChecker(){
			return consumer;
		}

	}

}
