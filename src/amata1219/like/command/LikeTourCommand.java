package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.ui.TourLikeListUI;
import org.bukkit.command.CommandSender;

public class LikeTourCommand implements BukkitCommandExecutor {

    public static final LikeTourCommand INSTANCE = new LikeTourCommand();

    private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> new TourLikeListUI().open(sender));

    @Override
    public CommandContext<CommandSender> executor() {
        return executor;
    }

}
