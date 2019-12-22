package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.slash.builder.ContextualExecutorBuilder;
import amata1219.slash.builder.Parser;
import static amata1219.slash.monad.Either.*;

public class LikeTeleportationAuthenticationCommand {
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> "You don't have like.like",
				s -> Parser.identity().parse(s).flatMap(token -> Main.INVITATION_TOKEN.equals(token) ? Success(token) : Failure(() -> "You don't have like.like")),
				ParserTemplates.like(() -> "You don't have like.like")
			)
			.execution(context -> sender -> {
				Like like = context.arguments.parsed(1);
				sender.teleport(like.hologram.getLocation());
				Main.plugin().config().teleportationText().apply(like).sendTo(sender);
			}).build();

}
