package amata1219.like.command;

import amata1219.like.Main;
import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.constant.Parsers;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.BranchContext;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.Like;
import amata1219.like.chunk.LikeMap;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LikeStatusCommand implements BukkitCommandExecutor {

	private final CommandContext<CommandSender> executor;

	{
		CommandContext<Player> description = define(
				() -> ChatColor.RED + "任意のLikeの説明文を書き換える：/likes desc [LikeのID] [説明文]",
				(sender, unparsedArguments, parsedArguments) -> {
					Like like = parsedArguments.poll();
					String desc = parsedArguments.poll();

					if (!like.isOwner(sender.getUniqueId())) {
						sender.sendMessage(ChatColor.RED + "所有していないLikeの説明文は編集できません。");
						return;
					}
					like.setDescription(desc);
					sender.sendMessage(ChatColor.GREEN + "Like(ID: " + like.id + ")の説明文を[" + ChatColor.RESET + like.description() + ChatColor.GREEN + "]に設定しました。");
				},
				ParserTemplates.like,
				Parsers.str
		);

		CommandContext<Player> move = define(
				() -> ChatColor.RED + "Likeを現在の位置に移動する：/likes move [LikeのID]",
				(sender, unparsedArguments, parsedArguments) -> {
					Like like = parsedArguments.poll();
					if (!like.isOwner(sender.getUniqueId())) {
						sender.sendMessage(ChatColor.RED + "所有していないLikeの移動はできません。");
						return;
					}

					LikeMap likeMap = Main.plugin().likeMap;
					likeMap.remove(like);
					like.teleportTo(sender.getLocation());
					likeMap.put(like);
					sender.sendMessage(ChatColor.GREEN + "Like(ID: " + like.id + ")を現在の位置に移動しました。");
				},
				ParserTemplates.like
		);

		BranchContext<Player> branches = define(
				() -> Joiner.on('\n').join(
						ChatColor.RED + "不正なコマンドが入力されたため実行出来ませんでした。",
						ChatColor.GRAY + "Likeの説明文を書き換える: /likes desc [like_id] [description]",
						ChatColor.GRAY + "Likeを現在地に移動する: /likes move [like_id]"
				),
				bind("desc", description),
				bind("description", description),
				bind("move", move)
		);

		executor = define(CommandSenderCasters.casterToPlayer, branches);
	}

	@Override
	public CommandContext<CommandSender> executor() {
		return executor;
	}

}
