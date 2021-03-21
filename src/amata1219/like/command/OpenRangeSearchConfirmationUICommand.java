package amata1219.like.command;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.listener.ControlLikeViewListener;
import amata1219.like.ui.LikeRangeSearchTpConfirmationUI;
import amata1219.like.ui.LikeRangeSearchingUI;
import org.bukkit.command.CommandSender;

public class OpenRangeSearchConfirmationUICommand implements BukkitCommandExecutor {

    private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> {
        ControlLikeViewListener listener = Main.plugin().controlLikeViewListener;
        if (!listener.viewersToRespawnPoints.containsKey(sender)) return;

        Like like = listener.viewersToLikesViewed.get(sender);
        LikeRangeSearchingUI ui = listener.viewersToUIs.get(sender);

        new LikeRangeSearchTpConfirmationUI(like, ui).open(sender);
    });

    @Override
    public CommandContext<CommandSender> executor() {
        return executor;
    }

}
