package amata1219.like.command;

import static amata1219.slash.monad.Either.Failure;
import static amata1219.slash.monad.Either.Success;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.slash.builder.Parser;
import amata1219.slash.effect.MessageEffect;

public class ParserTemplates {
	
	public static Parser<Like> like(MessageEffect error){
		return arg -> Parser.i64(error).parse(arg).flatMap(
			id -> Main.plugin().likes.containsKey(id) ? Success(Main.plugin().likes.get(id)) : Failure(() -> "&c-指定されたIDのLikeは存在しません。")
		);
	}

}
