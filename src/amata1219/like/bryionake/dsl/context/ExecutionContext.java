package amata1219.like.bryionake.dsl.context;

import amata1219.like.bryionake.adt.Either;
import amata1219.like.bryionake.adt.Either.*;
import amata1219.like.bryionake.dsl.argument.ParsedArgumentQueue;
import amata1219.like.bryionake.dsl.parser.FailableParser;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class ExecutionContext<S extends CommandSender> implements CommandContext<S> {

    private final Supplier<String> argumentNotFoundErrorMessage;
    private final List<FailableParser<?>> parsers;
    private final CommandContext<S> context;

    public ExecutionContext(Supplier<String> argumentNotFoundErrorMessage, List<FailableParser<?>> parsers, CommandContext<S> context) {
        this.argumentNotFoundErrorMessage = argumentNotFoundErrorMessage;
        this.parsers = parsers;
        this.context = context;
    }

    @Override
    public void execute(S sender, Queue<String> unparsedArguments, ParsedArgumentQueue parsedArguments) {
        if (unparsedArguments.isEmpty()) {
            sender.sendMessage(argumentNotFoundErrorMessage.get());
            return;
        }

        for (FailableParser<?> parser : parsers) {
            Either<String, ?> result = parser.tryParse(unparsedArguments.poll());
            if (result instanceof Failure) {
                String errorMessage = ((Failure<String, ?>) result).error;
                sender.sendMessage(errorMessage);
                return;
            }

            Object parsedArgument = ((Success<String, ?>) result).value;
            parsedArguments.offer(parsedArgument);
        }

        context.execute(sender, unparsedArguments, parsedArguments);
    }

}
