package amata1219.like.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import amata1219.like.Like;
import amata1219.like.Util;

public class LikeOpCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public void onCommand(CommandSender sender, Args args){
		switch(args.get()){
		case "move":
			if(Util.isNotPlayer(sender))
				return;

			Like move = Util.Likes.get(args.getNumber());
			if(move == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.move(move, Util.castPlayer(sender).getLocation());
			Util.tell(sender, ChatColor.GREEN, "Like(" + move.getId() + ")を移動しました。");
			break;
		case "delete":
			Like delete = Util.Likes.get(args.getNumber());
			if(delete == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.delete(delete);
			Util.tell(sender, ChatColor.GREEN, "Like(" + delete.getId() + ")を削除しました。");
			break;
		case "alldelete":
			OfflinePlayer target = Bukkit.getOfflinePlayer(args.get());
			if(target == null){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}

			UUID uuid = target.getUniqueId();
			if(!Util.Mines.containsKey(uuid)){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーはLikeを作成していません。");
				return;
			}

			Util.Mines.get(uuid).stream()
			.forEach(Util::delete);
			Util.tell(sender, ChatColor.GREEN, target.getName() + "が作成したLikeを全て削除しました。");
			break;
		case "reload":
			Util.Config.reload();
			Util.loadConfigValues();
			Util.tell(sender, ChatColor.GREEN, "コンフィグを再読み込みしました。");
			break;
		default:
			break;
		}
	}

}
