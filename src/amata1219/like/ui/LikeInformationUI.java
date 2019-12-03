package amata1219.like.ui;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;
import at.pcgamingfreaks.UUIDConverter;

public class LikeInformationUI implements InventoryUI, TouchHandler {

	private final MainConfig config = Main.instance().config();
	private final Like like;
	
	public LikeInformationUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x2, (p, l) -> {
			l.title = Text.of("&a-Like情報").color().toString();
			
			l.put(s -> {
				s.icon(i -> {
					i.material = Material.PLAYER_HEAD;
					UUID owner = like.creator();
					String playerName = UUIDConverter.getNameFromUUID(owner);
					i.displayName = Text.of("&a-%s").format(playerName).colored();
					i.raw = item -> ((SkullMeta) item.getItemMeta()).setOwningPlayer(Bukkit.getOfflinePlayer(owner));
				});
			}, 0);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.FAVORITES);
					i.displayName = Text.of("&a-お気に入りの数-&7-:-&f %s").format(like.favorites()).colored();
					i.amount = Math.min(like.favorites(), 64);
				});
			}, 3);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CREATION_TIMESTAMP);
					i.displayName = Text.of("&a-作成日時-&7-:-&f %s").format(like.creationTimestamp()).colored();
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.ID);
					i.displayName = Text.of("&a-管理ID-&7-:-&f %s").format(like.id).colored();
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.UNFAVORITE);
					i.displayName = Text.of("&a-お気に入りの解除").colored();
				});
			}, 6);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CREATORS_OTHER_LIKES);
					i.displayName = Text.of("&a-この作者の他のLike情報").colored();
				});
			}, 9);
			
			//todo set other likes
		});
	}

	@Override
	public void onTouch(Player player) {
		open(player);
	}
	
	/*
	 * 	UUID uuid = like.getOwner();
		Inventory inventory = createInventory(18, "Info@" + like.getStringId());

		ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) owner.getItemMeta();
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		meta.setDisplayName(ChatColor.GREEN + getName(uuid));
		meta.setOwningPlayer(player);
		owner.setItemMeta(meta);
		inventory.setItem(0, owner);
		inventory.setItem(3, newItem(LikeCount, "§aお気に入りの数:§f " + like.getLikeCount()));
		inventory.setItem(4, newItem(Timestamp, "§a作成日時:§f " + like.getCreationTimestamp()));
		inventory.setItem(5, newItem(Id, "§a管理ID:§f " + like.getId()));
		inventory.setItem(6, newItem(Unfavorite, "§aお気に入りの解除"));
		inventory.setItem(9, newItem(OtherLike, "§aこの作者の他のLike情報"));

		setOtherLike(inventory, like);
		return inventory;
	}
	 */

}
