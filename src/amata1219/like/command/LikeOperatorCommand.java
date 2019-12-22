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
import amata1219.like.config.LikeLimitDatabase;
import amata1219.like.config.MainConfig;
import amata1219.slash.ContextualExecutor;
import amata1219.slash.builder.ContextualExecutorBuilder;
import amata1219.slash.builder.Parser;
import amata1219.slash.contexts.PartiallyParsedArguments;
import amata1219.slash.effect.MessageEffect;
import amata1219.slash.executor.BranchedExecutor;
import amata1219.slash.executor.EchoExecutor;
import amata1219.slash.monad.Maybe;
import amata1219.slash.util.Text;
import amata1219.slash.util.Tuple;

public class LikeOperatorCommand {
	
	private static final MessageEffect movedescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likeop move [like_id] が有効です。"
			);
	
	private static final ContextualExecutor move = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				movedescription,
				ParserTemplates.like(() -> "&c-移動するLikeのIDを指定して下さい。")
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				like.teleportTo(sender.getLocation());
				Text.of("&a-Like(ID: %s)を現在地に移動しました。").apply(like.id).sendTo(sender);
			}).build();
	
	private static final MessageEffect deletedescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likeop delete [like_id] が有効です。"
			);
	
	private static final ContextualExecutor delete = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				deletedescription,
				ParserTemplates.like(() -> "削除するLikeのIDを指定して下さい。")
			).execution(context -> sender -> {
				Like like = context.arguments.parsed(0);
				like.delete(true);
				Text.of("&a-Like(ID: %s)を削除しました").apply(like.id).sendTo(sender);
			}).build();
			
	private static final MessageEffect deleteplayerdescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likeop deleteplayer [player_name] が有効です。"
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
			"&7-このコマンドは、/likeop deleteworld [world_name] が有効です。"
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
			"&7-このコマンドは、/likeop changeowner [like_id] [new_owner_name] が有効です。"
			);
	
	private static final ContextualExecutor changeowner = ContextualExecutorBuilder.playerCommandBuilder()
			.parsers(
				changeownerdescription,
				ParserTemplates.like(() -> "編集するLikeをIDを指定して下さい。"),
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
			"&7-このコマンドは、/likeop changeowner [old_owner_name] [new_owner_name] が有効です。"
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
		config.readAll();
		Text.of("&a-コンフィグを再読み込みしました。").sendTo(sender);
	}).build();
	
	private static final MessageEffect limitdescription = () -> Text.color(
			"&7-不正なコマンドが入力されたため実行出来ませんでした。",
			"&7-このコマンドは、/likeop limit [player] [operation] [limit] が有効です。"
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
			"&7-コンフィグをリロードする: /likeop reload"
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
				Tuple.of("limit", limit)
			);

}
