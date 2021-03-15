package amata1219.like.command;

import amata1219.bryionake.constant.CommandSenderCasters;
import amata1219.bryionake.dsl.BukkitCommandExecutor;
import amata1219.bryionake.dsl.context.CommandContext;
import amata1219.task.ChainedTask;
import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.sound.SoundEffects;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Random;

public class LikeRandomCommand implements BukkitCommandExecutor {

    private static final Random RANDOM = new Random();

    private final CommandContext<CommandSender> executor = define(CommandSenderCasters.casterToPlayer, (sender, unparsedArguments, parsedArguments) -> {
        Like like = selectLikeRandomly();
        if (like == null) {
            sender.sendMessage(ChatColor.RED + "このサーバーにLikeが1つも存在しないため実行出来ません。");
            return;
        }

        SoundEffects.SELECT_RANDOM_LIKE.play(sender);

        MainConfig config = Main.plugin().config();

        sender.sendMessage(new String[]{
                config.randomTeleportationMessage(),
                ChatColor.GRAY + like.description(),
                ChatColor.GRAY + "・ID：" + like.id,
                ChatColor.GRAY + "・お気に入り数：" + like.favorites(),
                ChatColor.GRAY + "・作成日時：" + like.creationTimestamp(),
                ChatColor.GRAY + "・座標：" + config.worldAlias(like.world()).or(() -> "Unknown") + ", " + like.x() + ", " + like.y() + ", " + like.z()
        });

        ChainedTask.asynchronously(10, () -> {
            String remainingSeconds = String.format("%.2f", config.randomTeleportationDelayedTicks() / 20.0f);
            sender.sendMessage(ChatColor.GREEN + "" + remainingSeconds + "秒後にテレポートします！");
        }).runTaskLaterSynchronously(config.randomTeleportationDelayedTicks(), () -> {
            sender.teleport(like.hologram.getLocation());
            SoundEffects.TELEPORTED_TO_RANDOM_LIKE.play(sender);
            sender.sendMessage(ChatColor.GREEN + "Like(ID: " + like.id + ")にテレポートしました！");
        });
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
