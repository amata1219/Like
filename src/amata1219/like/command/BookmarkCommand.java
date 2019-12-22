package amata1219.like.command;

import java.util.stream.Collectors;

import org.bukkit.command.CommandExecutor;

import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.ui.BookmarkUI;
import amata1219.slash.builder.ContextualExecutorBuilder;
import amata1219.slash.effect.MessageEffect;
import amata1219.slash.util.Text;

public class BookmarkCommand {
	
	private static final MessageEffect description = () -> Text.color(
			"&7-ブックマークを開く: /likeb [bookname]",
			"",
			"&7-ブックマーク一覧",
			Main.plugin().bookmarks.values().stream().map(bookmark -> "&7-・" + bookmark.name).collect(Collectors.joining("\n"))
			);
	
	public static final CommandExecutor executor = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				description,
				ParserTemplates.bookmark()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				new BookmarkUI(bookmark).open(sender);
			}).build();
	
}
