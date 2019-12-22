package amata1219.like.slash.executor;

import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.contexts.RawCommandContext;

public class PrintUsageExecutor implements ContextualExecutor {
	
	public static final PrintUsageExecutor executor = new PrintUsageExecutor();

	@Override
	public void executeWith(RawCommandContext context) {
		context.sender.sendMessage(context.command.aliasUsed);
	}

}
