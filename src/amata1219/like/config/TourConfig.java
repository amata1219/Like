package amata1219.like.config;

import amata1219.like.Like;
import amata1219.like.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class TourConfig extends Config {

    private boolean notificationIsEnabled;
    private String notificationMessage;
    private long notificationIntervalTicks;
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
        notificationIntervalTicks = config.getLong("Interval");

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
        config().set("Notification", notificationIsEnabled);
        update();
    }

    public List<Like> likes() {
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