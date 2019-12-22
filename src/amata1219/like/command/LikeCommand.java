package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.monad.Maybe;
import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.builder.ContextualExecutorBuilder;
import amata1219.like.slash.executor.BranchedExecutor;
import amata1219.like.slash.executor.EchoExecutor;
import amata1219.like.tuplet.Tuple;
import amata1219.like.ui.MyLikeListUI;
import amata1219.like.masquerade.text.Text;

public class LikeCommand {
	
	private static final ContextualExecutor status = ContextualExecutorBuilder.playerCommandBuilder().execution(context -> sender -> {
		new MyLikeListUI(sender.getUniqueId()).open(sender);
	}).build();
	
	private static final ContextualExecutor description = EchoExecutor.of(sender -> Text.of(
			"Likeを作成する: /like create",
			"&7-お気に入りのLikeの一覧を開く: /likel",
			"&7-作成したLikeの一覧を開く: /likel me"
			));
	
	public static final CommandExecutor executor = BranchedExecutor.of(
			Maybe.Some(description),
			Maybe.Some(description),
			Tuple.of("create", LikeCreationCommand.executor),
			Tuple.of("list", LikeListCommand.executor),
			Tuple.of("status", status)
			);
			
}
