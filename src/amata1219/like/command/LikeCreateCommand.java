package amata1219.like.command;

import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;

import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.masquerade.text.Text;
import amata1219.like.slash.dsl.ArgumentList;
import amata1219.like.slash.dsl.PlayerCommand;

public class LikeCreateCommand implements PlayerCommand {
	
	private final Main plugin = Main.plugin();
	private final MainConfig config = plugin.config();

	@Override
	public void onCommand(Player sender, ArgumentList<String> args) {
		if(!config.canLikesBeCreatedIn(sender.getWorld())){
			Text.of("&7-このワールドではLikeを作成出来ません。").accept(sender::sendMessage);
			return;
		}
		
		if(plugin.cooldownMap.contains(sender)){
			Text.of("&7-クールダウン中であるためLikeを作成出来ません。").accept(sender::sendMessage);
			return;
		}
		
	}
	
	/*
	 * 	public static void create(Player player){

		if(LikeMap.get(player.getLocation()).size() >= UpperLimit){
			tell(player, ChatColor.RED, "このチャンクではこれ以上Likeを作成出来ません。");
			return;
		}

		if(Mines.containsKey(uuid) && Mines.get(uuid).size() >= LimitConfig.get().getInt(uuid.toString())){
			tell(player, ChatColor.RED, "作成上限に達しているためこれ以上Likeを作成出来ません。");
			return;
		}

		NamedHologram hologram = new NamedHologram(player.getLocation().clone().add(0, 2, 0), String.valueOf(System.currentTimeMillis()));
		NamedHologramManager.addHologram(hologram);

		OldLike like = new OldLike(hologram, uuid);
		like.save(true);

		Likes.put(like.getId(), like);
		LikeMap.remove(like);
		addMine(like);
		LikeInvs.get(uuid).addMine(like);

		tell(player, ChatColor.GREEN, "Likeを作成しました。");

		cooldown.add(uuid);
		new BukkitRunnable(){
			@Override
			public void run(){
				cooldown.remove(uuid);
			}
		}.runTaskLater(OldMain.getPlugin(), CooldownTime);
	}
	 */

}
