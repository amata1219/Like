package amata1219.like.command;

import java.util.function.Supplier;

import org.bukkit.entity.Player;

import amata1219.like.Main;
import amata1219.like.masquerade.text.Text;
import amata1219.slash.dsl.ContextualExecutorBuilder;

public class LikeSCommand {
	
	//public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder().co
	
	/*
	 * 	private final Main plugin = Main.plugin();
	
	private final Supplier<String> error = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likes [move/lore/desc] が有効です。"
	);
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		args.next(error).match(
			Case("move").label(() -> args.nextLong(() -> "&c-移動するLikeのIDを指定して下さい。").flatMap(
				id -> plugin.likes.containsKey(id) ? Success(plugin.likes.get(id)) : Failure("&c-指定されたIDのLikeは存在しません")
			).flatMap(like -> {
				if(!like.isOwner(sender.getUniqueId())) return Failure("&c-他人のLikeは移動出来ません。");
				like.teleportTo(sender.getLocation());
				return Message("&a-Like(ID: " + like.id + ")を現在地に移動しました。");
			})),
			E1se(error)
		).onFailure(s -> Text.of(s).accept(sender::sendMessage));
	}
	 */

}
