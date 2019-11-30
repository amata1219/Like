package amata1219.like.ui;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import amata1219.like.Like;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;
import at.pcgamingfreaks.UUIDConverter;

public class LikeInformationUI implements InventoryUI {
	
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
					UUID owner = like.owner();
					String playerName = UUIDConverter.getNameFromUUID(owner);
					i.displayName = Text.of("&a-%s").format(playerName).toString();
					i.raw = item -> ((SkullMeta) item.getItemMeta()).setOwningPlayer(Bukkit.getOfflinePlayer(owner));
					
				});
			}, 0);
			
			l.put(s -> {
				s.icon(i -> {
					
				});
			}, 3);
		});
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
