package amata1219.like.command;

import static amata1219.slash.monad.Either.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.Order;
import amata1219.slash.builder.Parser;
import at.pcgamingfreaks.UUIDConverter;

public class ParserTemplates {
	
	public static Parser<Like> like(){
		return arg -> Parser.i64(() -> "LikeのIDは半角数字で入力して下さい").parse(arg).flatMap(
			id -> Main.plugin().likes.containsKey(id) ? Success(Main.plugin().likes.get(id)) : Failure(() -> "&c-指定されたLikeは存在しません。")
		);
	}
	
	public static Parser<OfflinePlayer> player(){
		return arg -> Parser.identity().parse(arg).flatMap(
			name -> {
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUIDConverter.getUUIDFromNameAsUUID(name, Bukkit.getOnlineMode()));
				return player != null ? Success(player) : Failure(() -> "&c-指定されたプレイヤーは存在しません。");
			}
		);
	}
	
	public static Parser<World> world(){
		return arg -> Parser.identity().parse(arg).flatMap(
			name -> {
				World world = Bukkit.getWorld(name);
				return world != null ? Success(world) : Failure(() -> "&c-指定されたワールドは存在しません。");
			}
		);
	}
	
	public static Parser<Bookmark> bookmark(){
		return arg -> Parser.identity().parse(arg).flatMap(
			name -> {
				Bookmark bookmark = Main.plugin().bookmarks.get(name);
				return bookmark != null ? Success(bookmark) : Failure(() -> "&c-指定されたブックマークは存在しません。");
			}
		);
	}
	
	public static Parser<Order> order(){
		return Parser.convert(() -> "&c-ソート順はnewestかoldestを指定して下さい。", arg -> Order.valueOf(arg.toUpperCase()));
	}

}
