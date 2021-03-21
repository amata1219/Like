package amata1219.like.listener;

import amata1219.like.Main;
import amata1219.like.sound.SoundEffects;
import amata1219.like.task.TaskRunner;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class EditingLikeDescriptionListener implements Listener {
	
	private final Main plugin = Main.plugin();
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();

		HashMap<UUID, Long> descriptionEditors = plugin.descriptionEditors;
		if (!descriptionEditors.containsKey(uniqueId)) return;
		
		event.setCancelled(true);

		Long targetLikeId = descriptionEditors.get(uniqueId);
		descriptionEditors.remove(uniqueId);

		if (targetLikeId == null || !plugin.likes.containsKey(targetLikeId)) {
			player.sendMessage(ChatColor.RED + "編集対象のLikeが削除されているため、編集をキャンセルしました。");
			return;
		}

		if (event.getMessage().equalsIgnoreCase("cancel")) {
			player.sendMessage(ChatColor.RED + "Like(ID: " + targetLikeId + ")の表示内容の編集をキャンセルしました。");
			SoundEffects.CANCEL.play(player);
			return;
		}

		String coloredMessage = ChatColor.translateAlternateColorCodes('&', event.getMessage());
		TaskRunner.runTaskSynchronously(task -> plugin.likes.get(targetLikeId).setDescription(coloredMessage));

		player.sendMessage(ChatColor.GREEN + "[Like]: Like(ID: " + targetLikeId + ")の表示内容を編集しました。");
		player.sendMessage(ChatColor.GREEN + "[Like]: 表示内容 > " + ChatColor.RESET + coloredMessage);

		SoundEffects.SUCCEEDED.play(player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		plugin.descriptionEditors.remove(event.getPlayer().getUniqueId());
	}

}
