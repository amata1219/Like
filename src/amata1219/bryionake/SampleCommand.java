package amata1219.bryionake;

import amata1219.bryionake.constant.CommandSenderCasters;
import amata1219.bryionake.constant.Parsers;
import amata1219.bryionake.dsl.BukkitCommandExecutor;
import amata1219.bryionake.dsl.context.BranchContext;
import amata1219.bryionake.dsl.context.CommandContext;
import amata1219.bryionake.dsl.context.ExecutionContext;
import amata1219.bryionake.dsl.parser.FailableParser;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SampleCommand implements BukkitCommandExecutor {

    private final CommandContext<CommandSender> executor;

    {
        ExecutionContext<Player> setHealth = define(
                () ->  "設定するHPを指定して下さい。",
                (sender, unparsedArguments, parsedArguments) -> {
                    int newHealth = parsedArguments.poll();
                    sender.setHealth(newHealth);
                },
                Parsers.i32
        );

        FailableParser<GameMode> gameMode = Parsers.define(
                GameMode::valueOf,
                () -> "ゲームモードは SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR の中から指定して下さい。"
        );

        ExecutionContext<Player> setGameMode = define(
                () -> "設定するゲームモードを指定して下さい。",
                (sender, unparsedArguments, parsedArguments) -> {
                    GameMode newGameMode = parsedArguments.poll();
                    sender.setGameMode(newGameMode);
                },
                gameMode
        );

        BranchContext<Player> setCommandBranches = define(
                () -> "引数には health または gamemode を指定して下さい。",
                bind("health", setHealth),
                bind("gamemode", setGameMode)
        );

        executor = define(CommandSenderCasters.casterToPlayer, setCommandBranches);
    }

    @Override
    public CommandContext<CommandSender> executor() {
        return executor;
    }

}
