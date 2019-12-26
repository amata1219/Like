package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Like;
import amata1219.like.masquerade.text.Text;
import amata1219.like.monad.Maybe;
import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.builder.ContextualExecutorBuilder;
import amata1219.like.slash.builder.Parser;
import amata1219.like.slash.executor.BranchedExecutor;
import amata1219.like.slash.executor.EchoExecutor;
import amata1219.like.tuplet.Tuple;

public class LikeStatusCommand {
	
	private static final ContextualExecutor description = EchoExecutor.of(sender -> Text.of(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-Likeの説明文を書き換える: /likes desc [like_id] [description]",
			"&7-Likeを現在地に移動する: /likes move [like_id]"
	).sendTo(sender));
	
	private static final ContextualExecutor desc = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color("&7-Likeの説明文を書き換える: /likes desc [like_id] [description]"),
				ParserTemplates.like(),
				Parser.identity()
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				String desc = context.arguments.parsed(1);
				if(!like.isOwner(sender.getUniqueId())){
					Text.of("&7-他人のLikeの説明文は編集できません。").sendTo(sender);
					return;
				}
				like.setDescription(desc);
				Text.of("&a-Like(%s)の説明文を-&r-%s-&r&a-に設定しました。").apply(like.id, like.description()).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor move = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color("&7-Likeを現在地に移動する: /likes move [like_id]"),
				ParserTemplates.like()
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				if(!like.isOwner(sender.getUniqueId())) {
					Text.of("&c-他人のLikeは移動出来ません。").sendTo(sender);
					return;
				}
				
				like.teleportTo(sender.getLocation());
				Text.of("&a-Like(ID: %s)を現在地に移動しました。").apply(like.id).sendTo(sender);
			}).build();
	
	public static final CommandExecutor executor = BranchedExecutor.of(
			Maybe.Some(description),
			Maybe.Some(description),
			Tuple.of("desc", desc),
			Tuple.of("move", move)
	);

}
