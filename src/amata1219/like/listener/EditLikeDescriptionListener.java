package amata1219.like.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.masquerade.text.Text;
import net.md_5.bungee.api.ChatColor;

public class EditLikeDescriptionListener implements Listener {
	
	private final Main plugin = Main.instance();
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		
		if(!plugin.descriptionEditors.containsKey(player)) return;
		
		e.setCancelled(true);
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			Like like = plugin.descriptionEditors.get(player);
			like.setDescription(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
			plugin.descriptionEditors.remove(player);
			Text.of("&a-Like(%s)の表示内容を編集しました。").format(like.id).color().sendTo(player);
		});
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		plugin.descriptionEditors.remove(e.getPlayer());
	}

}
