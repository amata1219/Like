package amata1219.like.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.Util;

public class OldLikeCommand implements CommandExecutor {

	@Override
	public void onCommand(CommandSender sender, Args args){
		if(Util.isNotPlayer(sender))
			return;

		Player player = Util.castPlayer(sender);
		switch(args.next()){
		case "create":
			Util.create(player);
			break;
		case "list":
			Util.status(player, args.next().equals("me"));
			break;
		case "status":
			Util.status(player, true);
			break;
		default:
			if(!args.get(0).equals(Util.TOKEN)){
				Util.tell(player, ChatColor.RED, "指定されたLikeは存在しません。");
				break;
			}

			if(!args.hasNextLong()){
				Util.tell(player, ChatColor.RED, "指定されたLikeは存在しません。");
				break;
			}

			long id = args.nextLong();
			if(!Util.Likes.containsKey(id)){
				Util.tell(player, ChatColor.RED, "指定されたLikeは存在しません。");
				break;
			}

			player.teleport(Util.Likes.get(id).getLocation(player.getLocation()));
			break;
		}
	}

}
