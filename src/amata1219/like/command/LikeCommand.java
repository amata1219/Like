package amata1219.like.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.Util;

public class LikeCommand implements CommandExecutor {

	@Override
	public void onCommand(CommandSender sender, Args args){
		if(Util.isNotPlayer(sender))
			return;

		Player player = Util.castPlayer(sender);
		switch(args.get(0)){

		}
	}

}
