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
		switch(args.get()){
		case "create":
			Util.create(player);
			break;
		case "list":
			Util.status(player, args.get().equals("me"));
			break;
		case "status":
			Util.status(player, true);
			break;
		default:
			if(!args.ref().equals(Util.TOKEN))
				break;

			long id = args.getNumber();
			if(!Util.Likes.containsKey(id))
				break;

			player.teleport(Util.Likes.get(id).getLocation(player.getLocation()));
			break;
		}
	}

}
