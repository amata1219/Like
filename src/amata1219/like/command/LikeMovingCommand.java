package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Like;
import amata1219.like.masquerade.text.Text;
import amata1219.slash.builder.ContextualExecutorBuilder;
import amata1219.slash.effect.MessageEffect;

public class LikeMovingCommand {
	
	private static final MessageEffect description = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likem [like_id] が有効です。"
		);
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				description,
				ParserTemplates.like(() -> "&c-移動するLikeのIDを指定して下さい")
			)
			.execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				if(!like.isOwner(sender.getUniqueId())) {
					Text.of("&c-他人のLikeは移動出来ません。").sendTo(sender);
					return;
				}
				
				like.teleportTo(sender.getLocation());
				Text.of("&a-Like(ID: %s)を現在地に移動しました。")
					.apply(like.id)
					.sendTo(sender);
			}).build();

}
