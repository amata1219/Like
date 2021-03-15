package amata1219.like.config;

import amata1219.like.Like;
import amata1219.like.Main;

import java.util.List;
import java.util.stream.Collectors;

public class TourConfig extends Config {

    private List<Like> likes;

    public TourConfig() {
        super("tour.yml");
        load();
    }

    @Override
    public void load() {
        likes = config().getStringList("Likes ids")
                .stream()
                .map(Long::parseLong)
                .filter(Main.plugin().likes::containsKey)
                .map(Main.plugin().likes::get)
                .collect(Collectors.toList());
    }

    public List<Like> likes() {
        return likes;
    }

}
