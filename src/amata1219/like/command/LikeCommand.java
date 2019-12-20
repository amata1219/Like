package amata1219.like.command;

import java.util.function.Supplier;

import org.bukkit.entity.Player;

import amata1219.like.masquerade.text.Text;
import amata1219.like.slash.dsl.ArgumentList;
import amata1219.like.slash.dsl.PlayerCommand;
import amata1219.like.ui.MyLikeListUI;

import static amata1219.like.slash.dsl.component.Matcher.*;

public class LikeCommand implements PlayerCommand {
	
	private final Supplier<String> error = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likes [move/lore/desc] が有効です。"
	);
	
	private final PlayerCommand likeCreate = new LikeCreateCommand(), likeList = new LikeListCommand();

	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		args.next(error).match(
			Case("create").then(() -> likeCreate.onCommand(sender, args)),
			Case("list").then(() -> likeList.onCommand(sender, args)),
			Case("status").then(() -> new MyLikeListUI(sender.getUniqueId()).open(sender))
			Else(() -> Result.)
		).onFailure(sender::sendMessage);
	}

	/*
	 * switch(args.next()){
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
	 */
	
}
