package amata1219.like.command;

import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.sound.SoundEffects;
import amata1219.like.task.TaskRunner;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Random;

public class LikeRandomCommand implements BukkitCommandExecutor {

    private static final Random RANDOM = new Random();

    private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> {
        Main plugin = Main.plugin();
        Economy economy = plugin.economy();
        MainConfig config = Main.plugin().config();
        double costs = config.randomTeleportationCosts();
        if (!economy.has(sender, costs)) {
            sender.sendMessage(ChatColor.RED + "テレポートコストが足りません。テレポートするには" + costs + "" + config.unitOfCost() + "必要です。");
            SoundEffects.FAILED.play(sender);
            return;
        }

        Like like = selectLikeRandomly();
        if (like == null) {
            sender.sendMessage(ChatColor.RED + "このサーバーにLikeが1つも存在しないため実行出来ません。");
            sender.sendMessage(ChatColor.RED + "※テレポートコストは消費されていません。");
            return;
        }

        economy.withdrawPlayer(sender, costs);
        economy.depositPlayer(Bukkit.getOfflinePlayer(like.owner()), costs);

        SoundEffects.PREPARED.play(sender);

        String remainingSeconds = String.format("%.1f", config.randomTeleportationDelayedTicks() / 20.0f);
        sender.sendMessage(new String[]{
                config.randomTeleportationMessage(),
                ChatColor.GRAY + "・説明文: " + ChatColor.RESET + like.description(),
                ChatColor.GRAY + "・ID: " + ChatColor.GREEN + like.id,
                ChatColor.GRAY + "・作成者: " + ChatColor.GREEN + like.ownerName(),
                ChatColor.GRAY + "・お気に入り数: " + ChatColor.GREEN + like.favorites(),
                ChatColor.GRAY + "・作成日時: " + ChatColor.GREEN + like.creationTimestamp(),
                ChatColor.GRAY + "・座標: " + ChatColor.GREEN + config.worldAlias(like.world()) + ", " + like.x() + ", " + like.y() + ", " + like.z(),
                ChatColor.RED + "" + costs + "" + config.unitOfCost() + "を消費しました。",
                ChatColor.GREEN + "" + remainingSeconds + "秒後にテレポートします！"
        });

        TaskRunner.runTaskLaterSynchronously(task -> {
            sender.teleport(like.hologram.getLocation());
            SoundEffects.SUCCEEDED.play(sender);
            sender.sendMessage(ChatColor.GREEN + "Like (ID: " + like.id + ") にテレポートしました！");
        }, config.randomTeleportationDelayedTicks());
    });

    private static Like selectLikeRandomly() {
        Collection<Like> likes = Main.plugin().likes.values();
        if (likes.isEmpty()) return null;

        int count = RANDOM.nextInt(likes.size());
        for (Like like : likes)
            if (count-- <= 0) return like;

        return null;
    }

    @Override
    public CommandContext<CommandSender> executor() {
        return executor;
    }

}
