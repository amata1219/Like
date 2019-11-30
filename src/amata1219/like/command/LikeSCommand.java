package amata1219.like.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.OldLike;
import amata1219.like.Util;

public class LikeSCommand  implements CommandExecutor {

	@Override
	public void onCommand(CommandSender sender, Args args){
		if(Util.isNotPlayer(sender)){
			sender.sendMessage(ChatColor.RED + "ゲーム内から実行して下さい。");
			return;
		}

		Player player = Util.castPlayer(sender);
		switch(args.next()){
		case "move":
			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "移動するLikeのIDを指定して下さい。。");
				return;
			}

			OldLike move = Util.Likes.get(args.nextLong());
			if(move == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			if(!player.getUniqueId().equals(move.getOwner())){
				Util.tell(sender, ChatColor.RED, "他人のLikeは移動出来ません。");
				return;
			}
			Util.move(move, Util.castPlayer(sender).getLocation());
			Util.tell(sender, ChatColor.GREEN, "Like(" + move.getId() + ")を移動しました。");
			break;
		case "lore":
			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "編集するLikeのIDを指定して下さい。。");
				return;
			}

			OldLike lore = Util.Likes.get(args.nextLong());
			if(lore == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			if(!player.getUniqueId().equals(lore.getOwner())){
				Util.tell(sender, ChatColor.RED, "他人のLikeは移動出来ません。");
				return;
			}

			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "説明文を入力して下さい。");
				return;
			}

			Util.changeLore(lore, Util.color(args.get(2, args.length())));
			Util.tell(player, ChatColor.GREEN, "Likeの表示内容を更新しました。");
			break;
		default:
			break;
		}
	}

}
