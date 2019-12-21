package amata1219.like.masquerade.text;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.command.CommandSender;

import com.google.common.base.Joiner;

public class Text {

	private static final String COLORS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	private static final String NULL = String.valueOf(Character.MIN_VALUE);
	
	public static Text of(String... texts){
		return new Text(coloring(Joiner.on('\n').join(texts)));
	}
	
	public static String color(String... texts){
		return of(texts).text;
	}
	
	private static String coloring(String text){
		char[] characters = text.toCharArray();

		for(int i = 0; i < characters.length - 1; i++){
			char color = characters[i + 1];

			if(characters[i] != '&' || COLORS.indexOf(color) <= -1) continue;

			if(i > 0 && characters[i - 1] == '-') characters[i - 1] = Character.MIN_VALUE;

			characters[i] = '§';
			characters[i + 1] = Character.toLowerCase(color);

			if(i < characters.length - 2 && characters[i + 2] == '-'){
				characters[i + 2] = Character.MIN_VALUE;
				i += 2;
			}else{
				i++;
			}
		}

		return new String(characters).replace(NULL, "");
	}
	
	protected String text;
	
	protected Text(String text){
		this.text = Objects.requireNonNull(text);
	}
	
	public String format(Object... objects){
		return String.format(text, objects);
	}
	
	public Text apply(Object... objects){
		text = format(objects);
		return this;
	}
	
	public void accept(Consumer<String> action){
		action.accept(text);
	}
	
	public void sendTo(CommandSender receiver){
		receiver.sendMessage(text);
	}
	
	@Override
	public Text clone(){
		return new Text(text);
	}
	
	@Override
	public String toString(){
		return text;
	}

}
