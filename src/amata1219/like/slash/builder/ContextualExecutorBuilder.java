package amata1219.like.slash.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.contexts.ParsedArgumentCommandContext;
import amata1219.like.slash.contexts.PartiallyParsedArguments;
import amata1219.like.slash.contexts.RawCommandContext;
import amata1219.like.slash.effect.MessageEffect;
import amata1219.like.slash.effect.TargetedEffect;
import amata1219.like.monad.Either;
import amata1219.like.monad.Maybe;
import amata1219.like.monad.Either.Failure;
import amata1219.like.monad.Either.Success;

public class ContextualExecutorBuilder<S extends CommandSender> {
	
	@SuppressWarnings("unchecked")
	public static <S extends CommandSender> ContextualExecutorBuilder<S> beginConfiguration(){
		return new ContextualExecutorBuilder<>(
			sender -> Maybe.Some((S) sender),
			(__, context) -> Maybe.Some(new PartiallyParsedArguments(new ArrayList<>(), new LinkedList<>(context.arguments))),
			__ -> ___ -> { }
		);
	}
	
	public final Function<CommandSender, Maybe<S>> senderTypeValidation;
	public final BiFunction<S, RawCommandContext, Maybe<PartiallyParsedArguments>> argumentsParser;
	public final Function<ParsedArgumentCommandContext<S>, TargetedEffect<S>> contextualExecution;
	
	public ContextualExecutorBuilder(
		Function<CommandSender, Maybe<S>> senderTypeValidation,
		BiFunction<S, RawCommandContext, Maybe<PartiallyParsedArguments>> argumentsParser,
		Function<ParsedArgumentCommandContext<S>, TargetedEffect<S>> contextualExecution
	){
		this.senderTypeValidation = senderTypeValidation;
		this.argumentsParser = argumentsParser;
		this.contextualExecution = contextualExecution;
	}
	
	public ContextualExecutorBuilder<S> parsers(MessageEffect onMissingArguments, Parser<?>... parsers){
		BiFunction<S, RawCommandContext, Maybe<PartiallyParsedArguments>> combinedParser = (sender, context) -> {
			Either<MessageEffect, PartiallyParsedArguments> result = parse(new LinkedList<>(Arrays.asList(parsers)), new LinkedList<>(context.arguments), new ArrayList<>(), onMissingArguments);
			if(result instanceof Success) return Maybe.Some(((Success<MessageEffect, PartiallyParsedArguments>) result).value);
			else {
				((Failure<MessageEffect, PartiallyParsedArguments>) result).error.sendTo(sender);
				return Maybe.None();
			}
		};
		return new ContextualExecutorBuilder<>(senderTypeValidation, combinedParser, contextualExecution);
	}
	
	private Either<MessageEffect, PartiallyParsedArguments> parse(Queue<Parser<?>> remainingParsers, Queue<String> remainingArguments, List<Object> accumulator, MessageEffect onMissingArguments){
		if(remainingParsers.isEmpty()) return Either.Success(new PartiallyParsedArguments(accumulator, remainingArguments));
		if(remainingArguments.isEmpty()) return Either.Failure(onMissingArguments);
		Parser<?> parser = remainingParsers.poll();
		String arg = remainingArguments.poll();
		return parser.parse(arg).flatMap(result -> {
			accumulator.add(result);
			return parse(remainingParsers, remainingArguments, accumulator, onMissingArguments);
		});
	}
	
	public ContextualExecutor build(){
		return rawContext -> {
			senderTypeValidation.apply(rawContext.sender).flatMap(
				refinedSender -> argumentsParser.apply(refinedSender, rawContext).map(
				parsedArguments -> new ParsedArgumentCommandContext<>(refinedSender, rawContext.command, parsedArguments)).apply(
				context -> contextualExecution.apply(context).apply(context.sender)
			));
		};
	}
	
	public ContextualExecutorBuilder<S> execution(Function<ParsedArgumentCommandContext<S>, TargetedEffect<S>> execution){
		return new ContextualExecutorBuilder<>(senderTypeValidation, argumentsParser, execution);
	}
	
	public <T extends S> ContextualExecutorBuilder<T> refineSenderWithError(String... errors){
		String error = Arrays.stream(errors).collect(Collectors.joining("\n"));
		MessageEffect effect = () -> error;
		return refineSender(effect);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends S> ContextualExecutorBuilder<T> refineSender(MessageEffect error){
		Function<CommandSender, Maybe<S>> newSenderTypeValidation = sender -> {
			return senderTypeValidation.apply(sender).flatMap(
				refined -> {
					try{
						return Maybe.Some((T) refined);
					}catch(Exception e){
						error.sendTo(sender);
						return Maybe.None();
					}
				}
			);
		};
		return (ContextualExecutorBuilder<T>) new ContextualExecutorBuilder<S>(
			newSenderTypeValidation,
			(BiFunction<S, RawCommandContext, Maybe<PartiallyParsedArguments>>) argumentsParser,
			(Function<ParsedArgumentCommandContext<S>, TargetedEffect<S>>) contextualExecution
		);
	}
	
	public static ContextualExecutorBuilder<Player> playerCommandBuilder(){
		return beginConfiguration().refineSenderWithError("&c-このコマンドはゲーム内から実行して下さい。");
	}
	
}
