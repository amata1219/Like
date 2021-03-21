package amata1219.like.config;

import amata1219.like.Like;
import amata1219.like.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TourConfig extends Config {

    private boolean notificationIsEnabled;
    private String notificationMessage;
    private long notificationIntervalTicks;

    private String guideMessage;
    private long guideDelayTicks;

    private List<Like> likes;

    public TourConfig() {
        super("tour.yml");
        load();
    }

    @Override
    public void load() {
        FileConfiguration config = config();

        ConfigurationSection notification = config.getConfigurationSection("Notification");
        notificationIsEnabled = notification.getBoolean("Enabled");
        notificationMessage = color(notification.getString("Message"));
        notificationIntervalTicks = notification.getInt("Interval") * 60 * 20;

        ConfigurationSection guide = config.getConfigurationSection("Guide");
        guideMessage = color(guide.getString("Message"));
        guideDelayTicks = guide.getLong("Delay") * 20;

        likes = config.getStringList("Likes ids")
                .stream()
                .map(Long::parseLong)
                .filter(Main.plugin().likes::containsKey)
                .map(Main.plugin().likes::get)
                .collect(Collectors.toList());
    }

    public boolean notificationIsEnabled() {
        return notificationIsEnabled;
    }

    public String notificationMessage() {
        return notificationMessage;
    }

    public long notificationIntervalTicks() {
        return notificationIntervalTicks;
    }

    public void setNotificationIsEnabled(boolean notificationIsEnabled) {
        this.notificationIsEnabled = notificationIsEnabled;
        config().set("Notification.Enabled", notificationIsEnabled);
        update();
    }

    public String guideMessage() {
        return guideMessage;
    }

    public long guideDelayTicks() {
        return guideDelayTicks;
    }

    public List<Like> likes() {
        int before = likes.size();
        likes.removeIf(like -> !Main.plugin().likes.containsKey(like.id));
        if (before > likes.size()) setLikes(likes);
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
        config().set(
                "Likes ids",
                likes.stream()
                .map(like -> like.id)
                .collect(Collectors.toList())
        );
        update();
    }

}
