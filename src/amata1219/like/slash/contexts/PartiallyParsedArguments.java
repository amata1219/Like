package amata1219.like.slash.contexts;

import java.util.List;
import java.util.Queue;

public class PartiallyParsedArguments {
	
	public final List<Object> parsed;
	public final Queue<String> unparsed;
	
	public PartiallyParsedArguments(List<Object> parsed, Queue<String> unparsed){
		this.parsed = parsed;
		this.unparsed = unparsed;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T parsed(int index){
		return (T) parsed.get(index);
	}

}
