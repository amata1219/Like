package amata1219.like.command;

import java.util.Queue;

import org.bukkit.command.CommandExecutor;

import amata1219.slash.dsl.ContextualExecutorBuilder;
import amata1219.slash.dsl.MessageEffect;
import amata1219.like.masquerade.text.Text;
import amata1219.like.ui.MyFavoriteLikeListUI;
import amata1219.like.ui.MyLikeListUI;

public class LikeLCommand {
	
	private static final MessageEffect description = () -> Text.color(
		"&7-お気に入りのLikeの一覧を開く: /likel",
		"&7-作成したLikeの一覧を開く: /likel me"
	);
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder().execution(context -> sender -> {
		Queue<String> args = context.arguments.unparsed;
		switch(args.isEmpty() ? "favorite" : args.poll()){
		case "me":
			new MyLikeListUI(sender.getUniqueId()).open(sender);
			break;
		case "favorite":
			new MyFavoriteLikeListUI(sender.getUniqueId()).open(sender);
			break;
		default:
			description.sendTo(sender);
			break;
		}
	}).build();

}
