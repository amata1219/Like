package amata1219.like.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import amata1219.like.Main;
import amata1219.like.monad.Either;
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
			Either.unit(plugin.descriptionEditors.get(player.getUniqueId()))
			.flatMap(id -> plugin.likes.containsKey(id) ? Either.Success(plugin.likes.get(id)) : Either.Failure(Text.color("&c-編集対象のLikeは削除されています。")))
			.onSuccess(like -> {
				String message = e.getMessage();
				if(message.equals("cancel")){
					player.sendMessage(Text.color("&c-Like(%s)の表示内容の編集をキャンセルしました。"));
				}else{
					like.setDescription(ChatColor.translateAlternateColorCodes('&', message));
					player.sendMessage(Text.of("&a-Like(%s)の表示内容を編集しました。").format(like.id));
				}
				plugin.descriptionEditors.remove(player);
			})
			.onFailure(player::sendMessage);
		});
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		plugin.descriptionEditors.remove(e.getPlayer().getUniqueId());
	}

}
