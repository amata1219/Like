package amata1219.like.command;

import org.bukkit.command.CommandSender;

import amata1219.like.Util;

public interface CommandExecutor {

	public default void onCommand(CommandSender sender, Args args){
		if(Util.isNotPlayer(sender))
			return;
	}

	public final class Args {

		public final String[] args;
		private int index = -1;

		public Args(String[] args){
			this.args = args;
		}

		public String get(){
			index++;
			return get(index);
		}

		public String ref(){
			return get(index);
		}

		public long getNumber(){
			return Long.parseLong(get());
		}

		public boolean isNumber(){
			try{
				Long.parseLong(get(index));
			}catch(NumberFormatException e){
				return false;
			}
			return true;
		}

		private String get(int index){
			return index < args.length ? args[index] : "-1";
		}

	}
}
