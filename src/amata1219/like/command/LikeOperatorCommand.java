package amata1219.like.command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import amata1219.bryionake.constant.CommandSenderCasters;
import amata1219.bryionake.constant.Parsers;
import amata1219.bryionake.dsl.BukkitCommandExecutor;
import amata1219.bryionake.dsl.context.CommandContext;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.Order;
import amata1219.like.config.LikeLimitDatabase;
import amata1219.like.config.MainConfig;
import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.builder.ContextualExecutorBuilder;
import amata1219.like.slash.builder.Parser;
import amata1219.like.slash.contexts.PartiallyParsedArguments;
import amata1219.like.slash.effect.MessageEffect;
import amata1219.like.slash.executor.BranchedExecutor;
import amata1219.like.slash.executor.EchoExecutor;
import amata1219.like.tuplet.Tuple;
import amata1219.like.masquerade.text.Text;
import amata1219.like.monad.Maybe;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LikeOperatorCommand implements BukkitCommandExecutor {

	private final CommandContext<CommandSender> executor;

	private static String join(String... strs) {
		return Joiner.on('\n').join(strs);
	}

	{
		CommandContext<CommandSender> move = define(CommandSenderCasters.casterToPlayer, define(
				() -> join(
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。。",
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
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。。",
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
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。。",
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
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。。",
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
						ChatColor.RED + "正しいコマンドが入力されなかったため実行できませんでした。。",
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
	}

	private static final ContextualExecutor changedata = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
					() -> Text.color(
							"&7-不正なコマンドが入力されたため実行出来ませんでした。",
							"&7-プレイヤーの作成したLikeを新しいプレイヤーに引き継ぐ: /likeop changedata [old_owner_name] [new_owner_name]"
					),
				ParserTemps.player(),
				ParserTemps.player()
			).execution(context -> sender -> {
				OfflinePlayer old = context.arguments.parsed(0);
				OfflinePlayer player = context.arguments.parsed(1);
				AtomicInteger count = new AtomicInteger();
				new HashMap<>(Main.plugin().players.get(old.getUniqueId()).likes).values().forEach(like -> {
					like.setOwner(player.getUniqueId());
					count.incrementAndGet();
				});
				Text.of("&a-%sのLike(%s個)を%sに引き継ぎました。").apply(old.getName(), count.get(), player.getName()).sendTo(sender);
			}).build();
					
	private static final ContextualExecutor reload = ContextualExecutorBuilder.playerCommandBuilder().execution(context -> sender -> {
		MainConfig config = Main.plugin().config();
		config.reload();
		config.load();
		Text.of("&a-コンフィグを再読み込みしました。").sendTo(sender);
	}).build();
	
	private static final MessageEffect limitdescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-プレイヤーのLike作成上限数を指定値に書き換える: /likeop limit [player] set [limit]",
			"&7-プレイヤーのLike作成上限数を指定値だけ引き上げる: /likeop limit [player] add [amount_to_add]",
			"&7-プレイヤーのLike作成上限数を指定値だけ引き下げる: /likeop limit [player] sub [amount_to_sub]"
			);

	private static final ContextualExecutor limit = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				limitdescription,
				ParserTemps.player(),
				Parser.identity(),
				Parser.u32(limitdescription)
			).execution(context -> sender -> {
				PartiallyParsedArguments args = context.arguments;
				OfflinePlayer player = args.parsed(0);
				UUID uuid = player.getUniqueId();
				String operation = args.parsed(1);
				int operand = args.parsed(2);
				LikeLimitDatabase database = Main.plugin().likeLimitDatabase();
				int limit = database.read(uuid);
				switch(operation){
				case "set":
					limit = operand;
					break;
				case "add":
					limit += operand;
					break;
				case "sub":
					limit = Math.min(limit - operand, 0);
					break;
				default:
					limitdescription.sendTo(sender);
					return;
				}
				database.write(uuid, limit);
				database.update();
				Text.of("&a-%sのLike作成上限数を%sに設定しました。").apply(player.getName(), limit).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor bookdescription = EchoExecutor.of(sender -> Text.of(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-ブックマークを作成・削除する: /likeop book [create/delete] [book_name]",
			"&7-Likeを追加・削除する: /likeop book [add/remove] [book_name] [like_id]",
			"&7-ソートする: /likeop book sort [newest/oldest]"
			).sendTo(sender));
	
	private static final ContextualExecutor bookcreation = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークを作成する: /likeop book create [book_name]"
				), 
				Parser.identity()
			).execution(context -> sender -> {
				String name = context.arguments.parsed(0);
				HashMap<String, Bookmark> bookmarks = Main.plugin().bookmarks;
				if(bookmarks.containsKey(name)){
					Text.of("&c-指定された名前のブックマークが既に存在しています。").sendTo(sender);
					return;
				}
				bookmarks.put(name, new Bookmark(name));
				Text.of("&a-ブックマーク(%s)を作成しました。").apply(name).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor bookdeletion = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークを削除する: /likeop book delete [book_name]"
				), 
				ParserTemps.bookmark()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Main.plugin().bookmarkDatabase().remove(bookmark);
				Text.of("&c-ブックマーク(%s)を削除しました。").apply(bookmark.name).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor booklikeaddition = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークにLikeを追加する: /likeop book add [book_name] [like_id]"
				), 
				ParserTemps.bookmark(),
				ParserTemps.like()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Like like = context.arguments.parsed(1);
				if(bookmark.likes.contains(like)){
					Text.of("&c-このLikeは既に追加されています。").sendTo(sender);
					return;
				}
				bookmark.likes.add(like);
				Text.of("&a-ブックマーク(%s)にLike(ID: %s)を追加しました。").apply(bookmark.name, like.id).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor booklikeremoving = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークにLikeを追加する: /likeop book remove [book_name] [like_id]"
				), 
				ParserTemps.bookmark(),
				ParserTemps.like()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Like like = context.arguments.parsed(1);
				if(!bookmark.likes.contains(like)){
					Text.of("&c-このLikeは追加されていません。").sendTo(sender);
					return;
				}
				bookmark.likes.remove(like);
				Text.of("&c-ブックマーク(%s)からLike(ID: %s)を削除しました。").apply(bookmark.name, like.id).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor booksort = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークにLikeを追加する: /likeop book sort [book_name] [newest/oldest]"
				), 
				ParserTemps.bookmark(),
				ParserTemps.order()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Order order = context.arguments.parsed(1);
				bookmark.setOrder(order);
				Text.of("&a-ブックマーク(%s)のLike表示順を%sにしました。").apply(bookmark.name, order.toString().toLowerCase()).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor book = BranchedExecutor.of(
			Maybe.Some(bookdescription),
			Maybe.Some(bookdescription),
			Tuple.of("create", bookcreation),
			Tuple.of("delete", bookdeletion),
			Tuple.of("add", booklikeaddition),
			Tuple.of("remove", booklikeremoving),
			Tuple.of("sort", booksort)
			);

	private static final ContextualExecutor description = EchoExecutor.of(sender -> Text.of(
			"&7-Likeを現在地に移動する: /likeop move [like_id]",
			"&7-Likeを削除する: /likeop delete [like_id]",
			"&7-プレイヤーが作成したLikeを全削除する: /likeop deleteplayer [player_name]",
			"&7-ワールド内のLikeを全削除する: /likeop deleteworld [world_name]",
			"&7-Likeの所有者を変更する: /likeop changeowner [like_id] [new_owner_name]",
			"&7-プレイヤーの作成したLikeを新しいプレイヤーに引き継ぐ: /likeop changedata [old_owner_name] [new_owner_name]",
			"&7-プレイヤーのLike作成上限数を書き換える: /likeop limit [player] set [limit]",
			"&7-プレイヤーのLike作成上限数を引き上げる: /likeop limit [player] add [amount_to_add]",
			"&7-プレイヤーのLike作成上限数を引き下げる: /likeop limit [player] sub [amount_to_sub]",
			"&7-コンフィグをリロードする: /likeop reload",
			"&7-ブックマークを作成・削除する: /likeop book [create/delete] [book_name]",
			"&7-ブックマークに対してLikeを追加・削除する: /likeop book [add/remove] [book_name] [like_id]",
			"&7-ブックマークをソートする: /likeop book sort [book_name] [newest/oldest]"
			).sendTo(sender));
	
					
	public static final CommandExecutor executor = BranchedExecutor.of(
				Maybe.Some(description),
				Maybe.Some(description),
				Tuple.of("move", move),
				Tuple.of("delete", delete),
				Tuple.of("deleteplayer", deleteplayer),
				Tuple.of("deleteworld", deleteworld),
				Tuple.of("changeowner", changeowner),
				Tuple.of("changedata", changedata),
				Tuple.of("reload", reload),
				Tuple.of("limit", limit),
				Tuple.of("book", book)
			);

	@Override
	public CommandContext<CommandSender> executor() {
		return null;
	}
}
