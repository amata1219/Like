package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Like;
import amata1219.like.masquerade.text.Text;
import amata1219.like.slash.builder.ContextualExecutorBuilder;
import amata1219.like.slash.builder.Parser;
import amata1219.like.slash.effect.MessageEffect;

public class LikeStatusCommand {
	
	private static final MessageEffect description = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-Likeの説明文を書き換える: /likes desc [like_id] [description]",
			"&7-Likeを現在地に移動する: /likes move [like_id]"
		);
	
	private static final CommandExecutor desc = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				description,
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
				
			}).build();
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				description,
				ParserTemplates.like()
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
