package amata1219.like.command;

import java.util.UUID;

import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.masquerade.task.SyncTask;
import amata1219.like.masquerade.text.Text;
import amata1219.like.player.PlayerData;
import amata1219.like.slash.ContextualExecutor;
import amata1219.like.slash.builder.ContextualExecutorBuilder;

public class LikeCreationCommand {
	
	public static final ContextualExecutor executor = ContextualExecutorBuilder.playerCommandBuilder().execution(context -> sender -> {
		Main plugin = Main.plugin();
		MainConfig config = plugin.config();
		if(!config.canLikesBeCreatedIn(sender.getWorld())){
			Text.of("&c-このワールドではLikeを作成出来ません。").accept(sender::sendMessage);
			return;
		}
		
		if(plugin.cooldownMap.contains(sender)){
			Text.of("&c-クールダウン中であるためLikeを作成出来ません。").accept(sender::sendMessage);
			return;
		}
		
		UUID uuid = sender.getUniqueId();
		PlayerData data = plugin.players.get(uuid);
		if(data.likes.size() >= plugin.likeLimitDatabase().read(uuid)){
			Text.of("&c-作成上限に達している為これ以上Likeは作成出来ません。").accept(sender::sendMessage);
			return;
		}
		
		NamedHologram hologram = new NamedHologram(sender.getLocation().add(0, 2, 0), String.valueOf(System.currentTimeMillis()));
		NamedHologramManager.addHologram(hologram);
		hologram.refreshAll();
		
		Like like = new Like(hologram, uuid);
		like.save(true);
		
		plugin.likes.put(like.id, like);
		data.registerLike(like);
		
		Text.of("&a-Like(ID: %s)を作成しました。").apply(like.id).accept(sender::sendMessage);
		
		SyncTask.define(() -> plugin.cooldownMap.remove(uuid))
		.executeLater(plugin.config().numberOfSecondsOfLikeCreationCooldown());
	}).build();

}
