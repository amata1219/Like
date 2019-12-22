package amata1219.like.slash.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.contexts.RawCommandContext;
import amata1219.like.monad.Maybe;
import amata1219.like.tuplet.Tuple;

public class BranchedExecutor implements ContextualExecutor {
	
	private static final Maybe<ContextualExecutor> executor = Maybe.Some(PrintUsageExecutor.executor);
	
	@SafeVarargs
	public static BranchedExecutor of(
		Maybe<ContextualExecutor> whenArgumentInsufficient,
		Maybe<ContextualExecutor> whenBranchNotFound,
		Tuple<String, ContextualExecutor>... branches
	){
		Map<String, ContextualExecutor> branchMap = new HashMap<>();
		for(Tuple<String, ContextualExecutor> branch : branches) branchMap.put(branch.first, branch.second);
		return new BranchedExecutor(branchMap, whenArgumentInsufficient, whenBranchNotFound);
	}
	
	@SafeVarargs
	public static BranchedExecutor of(Tuple<String, ContextualExecutor>... branches){
		return of(executor, executor, branches);
	}
	
	private final Map<String, ContextualExecutor> branches;
	private final Maybe<ContextualExecutor> whenArgumentInsufficient;
	private final Maybe<ContextualExecutor> whenBranchNotFound;
	
	public BranchedExecutor(
		Map<String, ContextualExecutor> branches,
		Maybe<ContextualExecutor> whenArgumentInsufficient,
		Maybe<ContextualExecutor> whenBranchNotFound
	){
		this.branches = branches;
		this.whenArgumentInsufficient = whenArgumentInsufficient;
		this.whenBranchNotFound = whenBranchNotFound;
	}
	
	public BranchedExecutor(Map<String, ContextualExecutor> branches){
		this(branches, executor, executor);
	}
	
	@Override
	public void executeWith(RawCommandContext rawContext){
		List<String> args = rawContext.arguments;
		if(args.isEmpty()) {
			executeOptionally(rawContext, whenArgumentInsufficient);
			return;
		}
		
		ContextualExecutor branch = branches.get(args.get(0));
		if(branch == null){
			executeOptionally(rawContext, whenBranchNotFound);
			return;
		}
		
		RawCommandContext argumentShiftedContext = new RawCommandContext(rawContext.sender, rawContext.command, args.subList(1, args.size()));
		
		branch.executeWith(argumentShiftedContext);
	}
	
	private void executeOptionally(RawCommandContext rawContext, Maybe<ContextualExecutor> executor){
		executor.apply(e -> e.executeWith(rawContext));
	}

}
