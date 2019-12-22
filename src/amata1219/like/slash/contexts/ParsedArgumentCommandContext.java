package amata1219.like.slash.contexts;

import org.bukkit.command.CommandSender;

public class ParsedArgumentCommandContext<S extends CommandSender> {
	
	public final S sender;
	public final ExecutedCommand command;
	public final PartiallyParsedArguments arguments;
	
	public ParsedArgumentCommandContext(S sender, ExecutedCommand command, PartiallyParsedArguments arguments){
		this.sender = sender;
		this.command = command;
		this.arguments = arguments;
	}

}
