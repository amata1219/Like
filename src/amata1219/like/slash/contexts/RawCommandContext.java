package amata1219.like.slash.contexts;

import java.util.List;

import org.bukkit.command.CommandSender;

public class RawCommandContext {
	
	public final CommandSender sender;
	public final ExecutedCommand command;
	public final List<String> arguments;
	
	public RawCommandContext(CommandSender sender, ExecutedCommand command, List<String> arguments){
		this.sender = sender;
		this.command = command;
		this.arguments = arguments;
	}

}
