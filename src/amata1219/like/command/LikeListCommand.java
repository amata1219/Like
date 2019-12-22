package amata1219.like.command;

import java.util.Queue;

import amata1219.like.masquerade.text.Text;
import amata1219.like.ui.MyFavoriteLikeListUI;
import amata1219.like.ui.MyLikeListUI;
import amata1219.slash.ContextualExecutor;
import amata1219.slash.builder.ContextualExecutorBuilder;
import amata1219.slash.effect.MessageEffect;

public class LikeListCommand {
	
	private static final MessageEffect description = () -> Text.color(
		"&7-不正なコマンドが入力されたため実行出来ませんでした。",
		"&7-このコマンドは、/likel, /likel mine が有効です。"
	);
	
	public static final ContextualExecutor executor = ContextualExecutorBuilder.playerCommandBuilder().execution(context -> sender -> {
		Queue<String> args = context.arguments.unparsed;
		switch(args.isEmpty() ? "favorite" : args.poll()){
		case "mine":
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
