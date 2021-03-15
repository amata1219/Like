package amata1219.like.command;

import amata1219.bryionake.constant.CommandSenderCasters;
import amata1219.bryionake.constant.Parsers;
import amata1219.bryionake.dsl.BukkitCommandExecutor;
import amata1219.bryionake.dsl.context.BranchContext;
import amata1219.bryionake.dsl.context.CommandContext;
import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.Order;
import amata1219.like.config.LikeLimitDatabase;
import amata1219.like.config.MainConfig;
import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LikeOperatorCommand implements BukkitCommandExecutor {

	private final CommandContext<CommandSender> executor;

	private static String join(String... strs) {
		return Joiner.on('\n').join(strs);
	}

	{
		CommandContext<CommandSender> move = define(CommandSenderCasters.casterToPlayer, define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "Likeを現在地に移動する: /likeop move [LikeのID]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Like like = parsedArguments.poll();
					like.teleportTo(sender.getLocation());
					sender.sendMessage(ChatColor.GREEN + "Like(ID: " + like.id + ")を現在の位置に移動しました。");
				},
				ParserTemplates.like
		));

		CommandContext<CommandSender> delete = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
					ChatColor.GRAY + "Likeを削除する：/likeop delete [LikeのID]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Like like = parsedArguments.poll();
					like.delete(true);
					sender.sendMessage(ChatColor.RED + "Like(ID: " + like.id + ")を削除しました。");
				},
				ParserTemplates.like
		);

		CommandContext<CommandSender> deletePlayer = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.DARK_RED + "プレイヤーが作成したLikeを全削除する：/likeop deleteplayer [プレイヤー名]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					OfflinePlayer player = parsedArguments.poll();

					HashMap<Long, Like> playerLikes = Main.plugin().players.get(player.getUniqueId()).likes;
					if (playerLikes.isEmpty()) {
						sender.sendMessage(ChatColor.RED + player.getName() + "はLikeを作成していません。");
						return;
					}

					int deletedLikesCount = playerLikes.size();
					playerLikes.values().forEach(like -> like.delete(false));
					HologramDatabase.trySaveToDisk();

					sender.sendMessage(ChatColor.DARK_RED + player.getName() + "が作成したLike(" + deletedLikesCount + ")を全て削除しました。");
				},
				ParserTemplates.player
		);

		CommandContext<CommandSender> deleteWorld = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.DARK_RED + "ワールド内の全Likeを削除する：/likeop deleteworld [ワールド名]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					World world = parsedArguments.poll();
					AtomicInteger count = new AtomicInteger();
					for (Like like : new HashMap<>(Main.plugin().likes).values()) if (like.world().equals(world)) {
						like.delete(false);
						count.incrementAndGet();
					}
					HologramDatabase.trySaveToDisk();
					sender.sendMessage(ChatColor.DARK_RED + world.getName() + "ワールドに存在する全Like(" + count.get() + ")を削除しました。");
				},
				Parsers.world
		);

		CommandContext<CommandSender> changeOwner = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "Likeの所有者を変更する：/likeop changeowner [LikeのID] [新しい所有者のプレイヤー名]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Like like = parsedArguments.poll();
					OfflinePlayer player = parsedArguments.poll();
					if (like.isOwner(player.getUniqueId())) {
						sender.sendMessage(ChatColor.RED + player.getName() + "は指定されたLike(ID: " + like.id + ")の所有者です。");
						return;
					}
					like.setOwner(player.getUniqueId());
					sender.sendMessage(ChatColor.GREEN + "Like(ID: " + like.id + ")の所有者を" + player.getName() + "に変更しました。");
				},
				ParserTemplates.like,
				ParserTemplates.player
		);

		CommandContext<CommandSender> changePlayerData = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "指定したプレイヤーの全Likeの所有権を新しいプレイヤーに譲渡する：/likeop changedata [元のプレイヤーの名前] [引き継ぐプレイヤーの名前]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					OfflinePlayer oldOwner = parsedArguments.poll();
					OfflinePlayer newOwner = parsedArguments.poll();
					AtomicInteger count = new AtomicInteger();
					for (Like like : new HashMap<>(Main.plugin().players.get(oldOwner.getUniqueId()).likes).values()) {
						like.setOwner(newOwner.getUniqueId());
						count.incrementAndGet();
					}
					sender.sendMessage(ChatColor.GREEN + oldOwner.getName() + "のLike(" + count.get() + "個)を" + newOwner.getName() + "に引き継ぎました。");
				},
				ParserTemplates.player,
				ParserTemplates.player
		);

		CommandContext<CommandSender> reload = (sender, unparsedArguments, parsedArguments) -> {
			MainConfig config = Main.plugin().config();
			config.reload();
			config.load();
			sender.sendMessage(ChatColor.GREEN + "コンフィグを再読み込みしました。");
		};

		Supplier<String> limitDescription = () -> join(
				ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
				ChatColor.GRAY + "プレイヤーのLike作成上限数を指定値に書き換える：/likeop limit [player] set [limit]",
				ChatColor.GRAY + "プレイヤーのLike作成上限数を指定値だけ引き上げる：/likeop limit [player] add [amount_to_add]",
				ChatColor.GRAY + "プレイヤーのLike作成上限数を指定値だけ引き下げる：/likeop limit [player] sub [amount_to_sub]"
		);

		CommandContext<CommandSender> limit = define(
				limitDescription,
				(sender, unparsedArguments, parsedArguments) -> {
					OfflinePlayer player = parsedArguments.poll();
					String operation = parsedArguments.poll();
					int operand = parsedArguments.poll();
					LikeLimitDatabase database = Main.plugin().likeLimitDatabase();
					int limitation = database.read(player.getUniqueId());
					switch (operation) {
						case "set":
							limitation = operand;
							break;
						case "add":
							limitation += operand;
							break;
						case "sub":
							limitation = Math.min(limitation - operand, 0);
							break;
						default:
							sender.sendMessage(limitDescription.get());
							return;
					}
					database.write(player.getUniqueId(), limitation);
					database.update();
					sender.sendMessage(ChatColor.GREEN + player.getName() + "のLike作成上限数を" + limitation + "に変更しました。");
				},
				ParserTemplates.player,
				Parsers.str,
				Parsers.u32
		);

		CommandContext<CommandSender> createBookmark = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークを作成する：/likeop book create [ブックマーク名]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					String bookmarkName = parsedArguments.poll();
					HashMap<String, Bookmark> bookmarks = Main.plugin().bookmarks;
					if (bookmarks.containsKey(bookmarkName)) {
						sender.sendMessage(ChatColor.RED + "指定された名前のブックマークが既に存在しています。");
						return;
					}
					bookmarks.put(bookmarkName, new Bookmark(bookmarkName));
					sender.sendMessage(ChatColor.GREEN + "ブックマーク(" + bookmarkName + ")を作成しました。");
				},
				Parsers.str
		);

		CommandContext<CommandSender> deleteBookmark = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークを削除する：/likeop book delete [ブックマーク名]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Bookmark bookmark = parsedArguments.poll();
					Main.plugin().bookmarkDatabase().remove(bookmark);
					sender.sendMessage(ChatColor.RED + "ブックマーク(" + bookmark.name + ")を削除しました。");
				},
				ParserTemplates.bookmark
		);

		CommandContext<CommandSender> addLikeToBookmark = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークにLikeを追加する: /likeop book add [ブックマーク名] [LikeのID]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Bookmark bookmark = parsedArguments.poll();
					Like like = parsedArguments.poll();
					if (bookmark.likes.contains(like)) {
						sender.sendMessage(ChatColor.RED + "このLikeは既に追加されています。");
						return;
					}
					bookmark.likes.add(like);
					sender.sendMessage(ChatColor.GREEN + "ブックマーク(" + bookmark.name + ")にLike(ID: " + like.id + ")を追加しました。");
				},
				ParserTemplates.bookmark,
				ParserTemplates.like
		);

		CommandContext<CommandSender> removeLikeFromBookmark = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークからLikeを削除する: /likeop book remove [ブックマーク名] [LikeのID]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Bookmark bookmark = parsedArguments.poll();
					Like like = parsedArguments.poll();
					if (!bookmark.likes.contains(like)) {
						sender.sendMessage(ChatColor.RED + "このLikeは追加されていません。");
						return;
					}
					bookmark.likes.remove(like);
					sender.sendMessage(ChatColor.RED + "ブックマーク(" + bookmark.name + ")からLike(ID: " + like.id + ")を削除しました。");
				},
				ParserTemplates.bookmark,
				ParserTemplates.like
		);

		CommandContext<CommandSender> sortBookmarkLikes = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークにLikeを追加する：/likeop book sort [ブックマーク名] [newest/oldest]"
				),
				(sender, unparsedArguments, parsedArguments) -> {
					Bookmark bookmark = parsedArguments.poll();
					Order order = parsedArguments.poll();
					bookmark.setOrder(order);
					sender.sendMessage(ChatColor.GREEN + "ブックマーク(" + bookmark.name + ")のLike表示順を" + order.toString() + "にしました。");
				},
				ParserTemplates.bookmark,
				ParserTemplates.order
		);

		BranchContext<CommandSender> bookmarkCommandsBranches = define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。",
						ChatColor.GRAY + "ブックマークを作成・削除する: /likeop book [create/delete] [ブックマーク名]",
						ChatColor.GRAY + "Likeを追加・削除する: /likeop book [add/remove] [ブックマーク名] [LikeのID]",
						ChatColor.GRAY + "ソートする: /likeop book sort [ブックマーク名] [newest/oldest]"
				),
				bind("create", createBookmark),
				bind("delete", deleteBookmark),
				bind("add", addLikeToBookmark),
				bind("remove", removeLikeFromBookmark),
				bind("sort", sortBookmarkLikes)
		);

		executor = define(
				() -> join(
						ChatColor.GRAY + "Likeを現在地に移動する: /likeop move [like_id]",
						ChatColor.GRAY + "Likeを削除する: /likeop delete [like_id]",
						ChatColor.GRAY + "プレイヤーが作成したLikeを全削除する: /likeop deleteplayer [player_name]",
						ChatColor.GRAY + "ワールド内のLikeを全削除する: /likeop deleteworld [world_name]",
						ChatColor.GRAY + "Likeの所有者を変更する: /likeop changeowner [like_id] [new_owner_name]",
						ChatColor.GRAY + "プレイヤーの作成したLikeを新しいプレイヤーに引き継ぐ: /likeop changedata [old_owner_name] [new_owner_name]",
						ChatColor.GRAY + "プレイヤーのLike作成上限数を書き換える: /likeop limit [player] set [limit]",
						ChatColor.GRAY + "プレイヤーのLike作成上限数を引き上げる: /likeop limit [player] add [amount_to_add]",
						ChatColor.GRAY + "プレイヤーのLike作成上限数を引き下げる: /likeop limit [player] sub [amount_to_sub]",
						ChatColor.GRAY + "コンフィグをリロードする: /likeop reload",
						ChatColor.GRAY + "ブックマークを作成・削除する: /likeop book [create/delete] [book_name]",
						ChatColor.GRAY + "ブックマークに対してLikeを追加・削除する: /likeop book [add/remove] [book_name] [like_id]",
						ChatColor.GRAY + "ブックマークをソートする: /likeop book sort [book_name] [newest/oldest]"
				),
				bind("move", move),
				bind("delete", delete),
				bind("deleteplayer", deletePlayer),
				bind("deleteworld", deleteWorld),
				bind("changeowner", changeOwner),
				bind("changedata", changePlayerData),
				bind("reload", reload),
				bind("limit", limit),
				bind("book", bookmarkCommandsBranches)
		);
	}

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}
}
