package amata1219.like.command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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

public class LikeOperatorCommand {
	
	private static final MessageEffect movedescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-Likeを現在地に移動する: /likeop move [like_id]"
			);
	
	private static final ContextualExecutor move = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				movedescription,
				ParserTemplates.like()
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				like.teleportTo(sender.getLocation());
				Text.of("&a-Like(ID: %s)を現在地に移動しました。").apply(like.id).sendTo(sender);
			}).build();
	
	private static final MessageEffect deletedescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-Likeを削除する: /likeop delete [like_id]"
			);
	
	private static final ContextualExecutor delete = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				deletedescription,
				ParserTemplates.like()
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				like.delete(true);
				Text.of("&a-Like(ID: %s)を削除しました").apply(like.id).sendTo(sender);
			}).build();
			
	private static final MessageEffect deleteplayerdescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-プレイヤーが作成したLikeを全削除する: /likeop deleteplayer [player_name]"
			);
	
	private static final ContextualExecutor deleteplayer = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				deleteplayerdescription,
				ParserTemplates.player()
			).execution(context -> sender -> {
				OfflinePlayer player = context.arguments.parsed(0);
				String name = player.getName();
				UUID uuid = player.getUniqueId();
				HashMap<Long, Like> list = Main.plugin().players.get(uuid).likes;
				if(list.isEmpty()){
					Text.of("&c-%sはLikeを作成していません。").apply(name).sendTo(sender);
					return;
				}
				int deleted = list.size();
				list.values().forEach(like -> like.delete(false));
				HologramDatabase.trySaveToDisk();
				Text.of("&a-%sが作成したLike(%s個)を全て削除しました。").apply(name, deleted).sendTo(sender);
			}).build();
	
	private static final MessageEffect deleteworlddescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-ワールド内のLikeを全削除する: /likeop deleteworld [world_name]"
			);
	
	private static final ContextualExecutor deleteworld = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				deleteworlddescription,
				ParserTemplates.world()
			).execution(context -> sender -> {
				World world = context.arguments.parsed(0);
				AtomicInteger count = new AtomicInteger();
				new HashMap<>(Main.plugin().likes).values().forEach(like -> {
					if(like.world().equals(world)){
						like.delete(false);
						count.incrementAndGet();
					}
				});
				HologramDatabase.trySaveToDisk();
				Text.of("&a-%sワールドに存在するLike(%s個)を全て削除しました。").apply(world.getName(), count.get()).sendTo(sender);
			}).build();
	
	private static final MessageEffect changeownerdescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-Likeの所有者を変更する: /likeop changeowner [like_id] [new_owner_name]"
			);
	
	private static final ContextualExecutor changeowner = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				changeownerdescription,
				ParserTemplates.like(),
				ParserTemplates.player()
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				OfflinePlayer player = context.arguments.parsed(1);
				String name = player.getName();
				UUID uuid = player.getUniqueId();
				if(like.isOwner(uuid)){
					Text.of("&c-%sは指定されたLike(ID: %s)の所有者です。").apply(name, like.id).sendTo(sender);
					return;
				}
				like.setOwner(uuid);
				Text.of("&a-Like(ID: %s)のオーナーを%sに変更しました。").apply(like.id, name).sendTo(sender);
			}).build();
	
	private static final MessageEffect changedatadescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-プレイヤーの作成したLikeを新しいプレイヤーに引き継ぐ: /likeop changedata [old_owner_name] [new_owner_name]"
			);
	
	private static final ContextualExecutor changedata = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				changedatadescription,
				ParserTemplates.player(),
				ParserTemplates.player()
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
				ParserTemplates.player(),
				Parser.identity(),
				Parser.u32(limitdescription)
			).execution(context -> sender -> {
				PartiallyParsedArguments args = context.arguments;
				OfflinePlayer player = args.parsed(0);
				UUID uuid = player.getUniqueId();
				String operation = args.parsed(1);
				int operand = args.parsed(2);
				LikeLimitDatabase database = Main.plugin().likeLimitDatabase();
				int limit = database.limit(uuid);
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
				database.set(uuid, limit);
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
				Text.of("&c-ブックマーク(%s)を作成しました。").apply(name).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor bookdeletion = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークを削除する: /likeop book delete [book_name]"
				), 
				ParserTemplates.bookmark()
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
				ParserTemplates.bookmark(),
				ParserTemplates.like()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Like like = context.arguments.parsed(1);
				if(bookmark.likes.contains(like)){
					Text.of("&c-このLikeは既に追加されています。").sendTo(sender);
					return;
				}
				bookmark.likes.add(like);
				Text.of("&c-ブックマーク(%s)にLike(ID: %s)を追加しました。").apply(bookmark.name, like.id).sendTo(sender);
			}).build();
	
	private static final ContextualExecutor booklikeremoving = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				() -> Text.color(
					"&7-不正なコマンドが入力されたため実行出来ませんでした。",
					"&7-ブックマークにLikeを追加する: /likeop book remove [book_name] [like_id]"
				), 
				ParserTemplates.bookmark(),
				ParserTemplates.like()
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
				ParserTemplates.bookmark(),
				ParserTemplates.order()
			).execution(context -> sender -> {
				Bookmark bookmark = context.arguments.parsed(0);
				Order order = context.arguments.parsed(1);
				bookmark.setOrder(order);
				Text.of("&c-ブックマーク(%s)のLike表示順を%sにしました。").apply(bookmark.name, order.toString().toLowerCase()).sendTo(sender);
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
	
}
