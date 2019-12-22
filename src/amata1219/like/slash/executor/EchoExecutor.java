package amata1219.like.slash.executor;

import org.bukkit.command.CommandSender;

import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.contexts.RawCommandContext;
import amata1219.like.slash.effect.TargetedEffect;

public class EchoExecutor implements ContextualExecutor {
	
	public static EchoExecutor of(TargetedEffect<CommandSender> effect){
		return new EchoExecutor(effect);
	}
	
	private final TargetedEffect<CommandSender> effect;
	
	public EchoExecutor(TargetedEffect<CommandSender> effect){
		this.effect = effect;
	}

	@Override
	public void executeWith(RawCommandContext context) {
		effect.apply(context.sender);
	}

}
