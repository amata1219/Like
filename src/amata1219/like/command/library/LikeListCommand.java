package amata1219.like.command.library;

import amata1219.like.ui.MyFavoriteLikeListUI;
import amata1219.like.ui.MyLikeListUI;

public class LikeListCommand {
	
	private static final PlayerCommand me = (sender, __) -> new MyLikeListUI(sender.getUniqueId()).open(sender);
	
	public static final PlayerCommand executor = (sender, args) -> {
		switch(args.poll()){
		case "me":
			me.onCommand(sender, args);
			break;
		case "":
			new MyFavoriteLikeListUI(sender.getUniqueId()).open(sender);
			break;
		default:
			break;
		}
	};
	
}
