package amata1219.like.slash.dsl;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface Command extends CommandExecutor {
	
	void onCommand(CommandSender sender, ArgumentList<String> args);
	
	@Override
	default boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){
		onCommand(sender, new ArgumentList<>(args));
		return true;
	}

}
