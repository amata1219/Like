package amata1219.like.command;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import amata1219.bryionake.constant.CommandSenderCasters;
import amata1219.bryionake.dsl.BukkitCommandExecutor;
import amata1219.bryionake.dsl.context.CommandContext;
import amata1219.bryionake.dsl.context.ExecutionContext;
import amata1219.like.sound.SoundEffects;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;

import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.slash.effect.MessageEffect;
import amata1219.like.ui.BookmarkUI;
import amata1219.like.masquerade.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BookmarkCommand implements BukkitCommandExecutor {

	private final CommandContext<CommandSender> executor;

	{
		Supplier<String> errorMessage = () -> Joiner.on("\n").join(
				ChatColor.GREEN + "ブックマークを開く：/likeb [bookname]",
				ChatColor.GREEN + "ブックマーク一覧：",
				Main.plugin().bookmarks.values().stream()
						.map(bookmark -> ChatColor.GRAY + "・" + bookmark.name)
						.collect(Collectors.joining("\n"))
		);

		ExecutionContext<Player> openBookmark = define(
				errorMessage,
				(sender, unparsedArguments, parsedArguments) -> {
					Bookmark bookmark = parsedArguments.poll();
					new BookmarkUI(bookmark).open(sender);
					SoundEffects.OPEN_BOOKMARK_UI.play(sender);
				},
				ParserTemplates.bookmark
		);

		executor = define(CommandSenderCasters.casterToPlayer, openBookmark);
	}

	private static final MessageEffect description = () -> Text.color(
			"&7-ブックマークを開く: /likeb [bookname]",
			"",
			"&7-ブックマーク一覧",
			Main.plugin().bookmarks.values().stream().map(bookmark -> "&7-・" + bookmark.name).collect(Collectors.joining("\n"))
			);
	
	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
