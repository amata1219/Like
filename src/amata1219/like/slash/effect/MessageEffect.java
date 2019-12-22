package amata1219.like.slash.effect;

import org.bukkit.command.CommandSender;

import amata1219.like.masquerade.text.Text;

public interface MessageEffect {
	
	default void sendTo(CommandSender sender){
		Text.of(message()).sendTo(sender);
	}
	
	String message();
	
}
