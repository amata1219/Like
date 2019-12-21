package amata1219.like.command.library;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import amata1219.like.masquerade.text.Text;

public interface PlayerCommand extends CommandExecutor {
	
	@Override
	default boolean onCommand(CommandSender sender, org.bukkit.command.Command commad, String label, String[] args){
		if(sender instanceof Player) onCommand((Player) sender, new ArgumentQueue(args));
		else Text.of("&c-ゲーム内から実行して下さい。").sendTo(sender);
		return true;
	}
	
	void onCommand(Player sender, ArgumentQueue args);

}
