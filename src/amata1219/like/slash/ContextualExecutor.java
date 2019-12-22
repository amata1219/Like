package amata1219.like.slash;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import amata1219.like.slash.contexts.ExecutedCommand;
import amata1219.like.slash.contexts.RawCommandContext;

public interface ContextualExecutor extends CommandExecutor {
	
	void executeWith(RawCommandContext context);
	
	@Override
	default boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		RawCommandContext context = new RawCommandContext(sender, new ExecutedCommand(command, label), Arrays.asList(args));
		executeWith(context);
		return true;
	}

}
