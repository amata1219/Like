package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.ui.MyLikeListUI;
import com.google.common.base.Joiner;
import org.bukkit.command.CommandSender;

public class LikeCommand implements BukkitCommandExecutor {
	
	private final CommandContext<CommandSender> executor;

	{
		CommandContext<CommandSender> status = define(
				CommandSenderCasters.casterToPlayer,
				(sender, unparsedArguments, parsedArguments) -> new MyLikeListUI(sender.getUniqueId()).open(sender)
		);

		executor = define(
				() -> Joiner.on('\n').join(
					"&7-Likeを作成する: /like create, /likec",
					"&7-お気に入りのLikeの一覧を開く: /like list, /likel",
					"&7-作成したLikeの一覧を開く: /like status, /like list mine, /likel mine",
					"&7-Likeの説明文を書き換える: /likes desc [like_id] [description]",
					"&7-Likeを現在地に移動する: /likes move [like_id]",
					"&7-ブックマーク一覧を表示する: /likeb",
					"&7-指定したブックマークを開く: /likeb [book_name]"
				),
				bind("create", LikeCreationCommand.INSTANCE.executor()),
				bind("list", LikeListCommand.INSTANCE.executor()),
				bind("status", status)
		);
	}

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
