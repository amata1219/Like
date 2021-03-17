package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import org.bukkit.command.CommandSender;

public class LikeTourCommand implements BukkitCommandExecutor {

    private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> {

    });

    @Override
    public CommandContext<CommandSender> executor() {
        return null;
    }

}
