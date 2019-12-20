package amata1219.like.command;

import static amata1219.like.slash.dsl.component.Matcher.*;

import java.util.UUID;

import org.bukkit.entity.Player;

import amata1219.like.masquerade.text.Text;
import amata1219.like.monad.Result;
import amata1219.like.slash.dsl.ArgumentList;
import amata1219.like.slash.dsl.PlayerCommand;
import amata1219.like.ui.MyFavoriteLikeListUI;
import amata1219.like.ui.MyLikeListUI;

public class LikeListCommand implements PlayerCommand {
	
	public static final LikeListCommand INSTANCE = new LikeListCommand();
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		UUID uuid = sender.getUniqueId();
		args.nextOr(() -> "favorite").match(
			Case("me").then(() -> new MyLikeListUI(uuid).open(sender)),
			Case("favorite").then(() -> new MyFavoriteLikeListUI(uuid).open(sender)),
			Else(() -> Result.error(Text.color(
				"&7-不正なコマンドが入力されたため実行出来ませんでした。",
				"&7-このコマンドは、/likel, /likel me が有効です。"
			)))
		).onFailure(sender::sendMessage);
	}

}
