package amata1219.like.command;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.masquerade.text.Text;
import amata1219.like.monad.Result;
import amata1219.like.slash.dsl.ArgumentList;
import amata1219.like.slash.dsl.PlayerCommand;
import static amata1219.like.slash.dsl.component.Matcher.*;

import java.util.function.Supplier;

public class LikeStatusCommand implements PlayerCommand {
	
	private final Main plugin = Main.plugin();
	
	private final Supplier<String> usage = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likes [move/lore/desc] が有効です。"
	);

	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		args.next(usage).match(
			Case("move").label(() -> args.nextLong(() -> Text.color("移動するLikeのIDを指定して下さい。")).flatMap(id -> {
				Like like = plugin.likes.get(id);
				if(like == null) return Result.Failure("指定されたIDのLikeは存在しません");
				if(!like.isOwner(sender.getUniqueId())) return Result.Failure("他人のLikeは移動出来ません。");
				like.teleportTo(sender.getLocation());
				Text.of("&a-Like(ID: %s)を現在地に移動しました。").apply(like.id).accept(sender::sendMessage);
				return null;
			})),
			Else(() -> Result.error(usage.get()))
		).onFailure(sender::sendMessage);
	}
	
}
