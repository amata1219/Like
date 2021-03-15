package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.ui.MyFavoriteLikeListUI;
import amata1219.like.ui.MyLikeListUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LikeListCommand implements BukkitCommandExecutor {

	public static final LikeListCommand INSTANCE = new LikeListCommand();
	
	private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> {
		if (unparsedArguments.isEmpty() || unparsedArguments.peek().equalsIgnoreCase("favorite")) {
			new MyFavoriteLikeListUI(sender.getUniqueId()).open(sender);
		} else if (unparsedArguments.peek().equalsIgnoreCase("mine")) {
			new MyLikeListUI(sender.getUniqueId()).open(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "/likel または /likel mine を指定して下さい。");
		}
	});

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
