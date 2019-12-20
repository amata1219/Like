package amata1219.like.command;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;

import static amata1219.like.monad.Result.*;
import amata1219.like.slash.dsl.ArgumentList;
import amata1219.like.slash.dsl.PlayerCommand;
import static amata1219.like.slash.dsl.component.Matcher.*;

public class LikeTeleportCommand implements PlayerCommand {
	
	private final Main plugin = Main.plugin();
	
	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		args.next(() -> "").flatMap(
			token -> !token.equals(Main.INVITATION_TOKEN) ? Failure("") : args.nextLong(() -> "").flatMap(
			id -> {
				Like like = plugin.likes.get(id);
				if(like == null) return Failure("");
				sender.teleport(like.hologram.getLocation());
				plugin.config().teleportationText().apply(like).accept(sender::sendMessage);
				return None();
		}));
	}

}
