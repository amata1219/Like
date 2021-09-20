package amata1219.like.command;

import amata1219.like.bryionake.dsl.parser.FailableParser;
import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.Order;
import at.pcgamingfreaks.UUIDConverter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

import static amata1219.like.bryionake.adt.Either.success;
import static amata1219.like.bryionake.constant.Parsers.*;

public class ParserTemplates {

    public static final FailableParser<Like> like = i64.append(id -> {
        HashMap<Long, Like> likes = Main.plugin().likes;
        return likes.containsKey(id) ? success(likes.get(id)) : error("指定されたLikeは存在しません。");
    });

    public static final FailableParser<OfflinePlayer> player = str.append(name -> {
        UUID uuid = UUIDConverter.getUUIDFromNameAsUUID(name, true); //本来はBungeecord使っているかをチェックすべき。
        return Main.plugin().players.containsKey(uuid) ? success(Bukkit.getOfflinePlayer(uuid)) : error("指定されたプレイヤーは存在しません。");
    });

    public static final FailableParser<Bookmark> bookmark = arg -> {
        HashMap<String, Bookmark> bookmarks = Main.plugin().bookmarks;
        return bookmarks.containsKey(arg) ? success(bookmarks.get(arg)) : error("指定されたブックマークは存在しません。");
    };

    public static final FailableParser<Order> order = define(arg -> Order.valueOf(arg.toUpperCase()), () -> "ソート順は newest か oldest を指定して下さい");

}
