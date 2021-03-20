package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.constant.Parsers;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.Like;
import amata1219.like.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LikeTokenCommand implements BukkitCommandExecutor {

	private final CommandContext<CommandSender> executor;

	{
		CommandContext<Player> authenticate = define(
				() -> "",
				(sender, unparsedArguments, parsedArguments) -> {
					String token = parsedArguments.poll();
					if(!token.equals(Main.INVITATION_TOKEN)){
						sender.sendMessage("You don't have like.like");
						return;
					}

					Like like = parsedArguments.poll();
					sender.teleport(like.hologram.getLocation());
					Main.plugin().config().teleportationText().apply(like).sendTo(sender);
				},
				Parsers.str,
				ParserTemplates.like
		);

		executor = define(CommandSenderCasters.casterToPlayer, authenticate);
	}

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
