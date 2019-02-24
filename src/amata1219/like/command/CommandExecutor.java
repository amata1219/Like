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

		public Args(String[] args){
			this.args = args;
		}

		public String get(int index){
			return index < args.length ? args[index] : "";
		}

		public boolean isNumber(int index){
			try{
				Long.parseLong(get(index));
			}catch(NumberFormatException e){
				return false;
			}
			return true;
		}

		public long getNumber(int index){
			return Long.parseLong(get(index));
		}

	}
}
