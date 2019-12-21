package amata1219.like.command.library;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface Command extends CommandExecutor {
	
	@Override
	default boolean onCommand(CommandSender sender, org.bukkit.command.Command commad, String label, String[] args){
		onCommand(sender, new ArgumentQueue(args));
		return true;
	}
	
	void onCommand(CommandSender sender, ArgumentQueue args);

}
