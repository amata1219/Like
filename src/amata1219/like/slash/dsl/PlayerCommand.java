package amata1219.like.slash.dsl;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.masquerade.text.Text;

public interface PlayerCommand extends CommandExecutor {
	
	void onCommand(Player sender, ArgumentList<String> args);
	
	@Override
	default boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(Text.color("&c-ゲーム内から実行して下さい。"));
			return true;
		}
		
		onCommand((Player) sender, new ArgumentList<>(args));
		return true;
	}

}
