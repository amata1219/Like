package amata1219.like.command;

import org.bukkit.command.CommandSender;

import amata1219.like.Util;

public interface CommandExecutor {

	public default void onCommand(CommandSender sender, Args args){
		if(Util.isNotPlayer(sender))
			return;
	}

}
