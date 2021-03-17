package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.ui.MyLikeListUI;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
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
					ChatColor.GRAY + "Likeを作成する: /like create, /likec",
					ChatColor.GRAY + "お気に入りのLikeの一覧を開く: /like list, /likel",
					ChatColor.GRAY + "作成したLikeの一覧を開く: /like status, /like list mine, /likel mine",
					ChatColor.GRAY + "Likeの説明文を書き換える: /likes desc [like_id] [description]",
					ChatColor.GRAY + "Likeを現在地に移動する: /likes move [like_id]",
					ChatColor.GRAY + "ブックマーク一覧を表示する: /likeb",
					ChatColor.GRAY + "指定したブックマークを開く: /likeb [book_name]",
					ChatColor.GRAY + "ツアー用UIを開く：/like tour"
				),
				bind("create", LikeCreationCommand.INSTANCE.executor()),
				bind("list", LikeListCommand.INSTANCE.executor()),
				bind("status", status),
				bind("tour", LikeTourCommand.INSTANCE.executor())
		);
	}

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
