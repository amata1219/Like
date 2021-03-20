package amata1219.like.bryionake.dsl.context;

import amata1219.like.bryionake.adt.Either;
import amata1219.like.bryionake.adt.Either.*;
import amata1219.like.bryionake.dsl.argument.ParsedArgumentQueue;
import amata1219.like.bryionake.dsl.caster.SafeCaster;
import org.bukkit.command.CommandSender;

import java.util.Queue;

public class CastingCommandSenderContext<S extends CommandSender, T extends S> implements CommandContext<S> {

    private final SafeCaster<S, T, String> caster;
    private final CommandContext<T> context;

    public CastingCommandSenderContext(SafeCaster<S, T, String> caster, CommandContext<T> context) {
        this.caster = caster;
        this.context = context;
    }

    @Override
    public void execute(S sender, Queue<String> unparsedArguments, ParsedArgumentQueue parsedArguments) {
        Either<String, T> result = caster.tryCast(sender);
        if (result instanceof Failure) {
            String errorMessage = ((Failure<String, T>) result).error;
            sender.sendMessage(errorMessage);
            return;
        }

        T castedSender = ((Success<String, T>) result).value;
        context.execute(castedSender, unparsedArguments, parsedArguments);
    }

}
