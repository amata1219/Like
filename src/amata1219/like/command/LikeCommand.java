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
			"&7-このコマンドは、/like create, /like list, /like list me, /like status が有効です。"
	);
	
	private final PlayerCommand likeCreate = new LikeCreateCommand(), likeList = new LikeListCommand();

	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		args.next(error).match(
			Case("create").then(() -> likeCreate.onCommand(sender, args)),
			Case("list").then(() -> likeList.onCommand(sender, args)),
			Case("status").then(() -> new MyLikeListUI(sender.getUniqueId()).open(sender)),
			E1se(error)
		).onFailure(sender::sendMessage);
	}

}
