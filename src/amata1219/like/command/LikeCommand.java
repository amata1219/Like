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
			"&7-Likeを作成する: /like create, /likec",
			"&7-お気に入りのLikeの一覧を開く: /like list, /likel",
			"&7-作成したLikeの一覧を開く: /like status, /like list mine, /likel mine",
			"&7-Likeの説明文を書き換える: /likes desc [like_id] [description]",
			"&7-Likeを現在地に移動する: /likes move [like_id]",
			"&7-ブックマーク一覧を表示する: /likeb",
			"&7-指定したブックマークを開く: /likeb [book_name]"
	).sendTo(sender));
	
	public static final CommandExecutor executor = BranchedExecutor.of(
			Maybe.Some(description),
			Maybe.Some(description),
			Tuple.of("create", LikeCreationCommand.executor),
			Tuple.of("list", LikeListCommand.executor),
			Tuple.of("status", status)
	);

}
