package amata1219.like.bryionake.dsl;

import amata1219.like.bryionake.adt.Pair;
import amata1219.like.bryionake.constant.Constants;
import amata1219.like.bryionake.dsl.argument.ParsedArgumentQueue;
import amata1219.like.bryionake.dsl.caster.SafeCaster;
import amata1219.like.bryionake.dsl.context.BranchContext;
import amata1219.like.bryionake.dsl.context.CastingCommandSenderContext;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.bryionake.dsl.context.ExecutionContext;
import amata1219.like.bryionake.dsl.parser.FailableParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;

public interface BukkitCommandExecutor extends CommandExecutor {

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        executor().execute(sender, new ArrayDeque<>(Arrays.asList(args)), new ParsedArgumentQueue());
        return true;
    }

    default <S extends CommandSender> ExecutionContext<S> define(Supplier<String> argumentNotFoundErrorMessage, CommandContext<S> context, FailableParser<?>... parsers) {
        return new ExecutionContext<>(() -> prefixErrorMessage(argumentNotFoundErrorMessage), Arrays.asList(parsers), context);
    }

    default <S extends CommandSender> BranchContext<S> define(Supplier<String> argumentNotFoundErrorMessage, Pair<String, CommandContext<S>>... branches) {
        HashMap<String, CommandContext<S>> contexts = new HashMap<>();
        for (Pair<String, CommandContext<S>> branch : branches) contexts.put(branch.left, branch.right);
        return new BranchContext<>(() -> prefixErrorMessage(argumentNotFoundErrorMessage), contexts);
    }

    default <S extends CommandSender> Pair<String, CommandContext<S>> bind(String label, CommandContext<S> context) {
        return new Pair<>(label, context);
    }

    default <S extends CommandSender, T extends S> CastingCommandSenderContext<S, T> define(SafeCaster<S, T, String> caster, CommandContext<T> context) {
        return new CastingCommandSenderContext<>(caster, context);
    }

    default String prefixErrorMessage(Supplier<String> errorMessage) {
        return Constants.ERROR_MESSAGE_PREFIX + errorMessage.get();
    }

    CommandContext<CommandSender> executor();

}
