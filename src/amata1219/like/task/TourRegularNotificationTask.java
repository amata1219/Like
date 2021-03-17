package amata1219.like.task;

import amata1219.like.config.TourConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TourRegularNotificationTask implements Runnable {

    private final TourConfig config;
    private final TextComponent message;

    public TourRegularNotificationTask(TourConfig config) {
        this.config = config;
        this.message = new TextComponent(config.notificationMessage());
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/like tour"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "クリックで /like tour コマンドを実行しツアー専用UIを開きます！")));

    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) player.spigot().sendMessage(message);
    }

}
