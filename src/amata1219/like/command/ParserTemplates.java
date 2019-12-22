package amata1219.like.command;

import static amata1219.slash.monad.Either.Failure;
import static amata1219.slash.monad.Either.Success;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.slash.builder.Parser;
import amata1219.slash.effect.MessageEffect;
import at.pcgamingfreaks.UUIDConverter;

public class ParserTemplates {
	
	public static Parser<Like> like(MessageEffect error){
		return arg -> Parser.i64(error).parse(arg).flatMap(
			id -> Main.plugin().likes.containsKey(id) ? Success(Main.plugin().likes.get(id)) : Failure(() -> "&c-指定されたIDのLikeは存在しません。")
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

}
