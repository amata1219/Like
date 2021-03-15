package amata1219.like.bryionake.dsl.context;

import amata1219.like.bryionake.dsl.argument.ParsedArgumentQueue;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Supplier;

public class BranchContext<S extends CommandSender> implements CommandContext<S> {

    private final Supplier<String> argumentNotFoundErrorMessage;
    private final HashMap<String, CommandContext<S>> contexts;

    public BranchContext(Supplier<String> argumentNotFoundErrorMessage, HashMap<String, CommandContext<S>> contexts) {
        this.argumentNotFoundErrorMessage = argumentNotFoundErrorMessage;
        this.contexts = contexts;
    }

    @Override
    public void execute(S sender, Queue<String> unparsedArguments, ParsedArgumentQueue parsedArguments) {
        if (unparsedArguments.isEmpty() || !contexts.containsKey(unparsedArguments.peek())) {
            sender.sendMessage(argumentNotFoundErrorMessage.get());
            return;
        }

        contexts.get(unparsedArguments.poll()).execute(sender, unparsedArguments, parsedArguments);
    }

}
