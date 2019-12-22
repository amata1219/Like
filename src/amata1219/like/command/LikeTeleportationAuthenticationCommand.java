package amata1219.like.command;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.slash.builder.ContextualExecutorBuilder;
import amata1219.like.slash.builder.Parser;

public class LikeTeleportationAuthenticationCommand {
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> "You don't have like.like",
				Parser.identity(),
				ParserTemplates.like()
			)
			.execution(context -> sender -> {
				String token = context.arguments.parsed(0);
				if(!token.equals(Main.INVITATION_TOKEN)){
					sender.sendMessage("You don't have like.like");
					return;
				}
				
				Like like = context.arguments.parsed(1);
				sender.teleport(like.hologram.getLocation());
				Main.plugin().config().teleportationText().apply(like).sendTo(sender);
			}).build();

}
