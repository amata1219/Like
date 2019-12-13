package amata1219.like.ui;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;
import at.pcgamingfreaks.UUIDConverter;

public class LikeEditingUI implements InventoryUI {
	
	private final MainConfig config = Main.instance().config();
	private final Like like;
	
	public LikeEditingUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "Likeの編集";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = Material.PLAYER_HEAD;
					UUID owner = like.owner();
					String playerName = UUIDConverter.getNameFromUUID(owner);
					i.displayName = Text.of("&a-%s").apply(playerName);
					i.raw = item -> ((SkullMeta) item.getItemMeta()).setOwningPlayer(Bukkit.getOfflinePlayer(owner));
				});
			}, 0);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.FAVORITES);
					i.displayName = Text.of("&a-お気に入りの数-&7-:-&f %s").apply(like.favorites());
					i.amount = Math.min(like.favorites(), 64);
				});
			}, 3);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CREATION_TIMESTAMP);
					i.displayName = Text.of("&a-作成日時-&7-:-&f %s").apply(like.creationTimestamp());
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.ID);
					i.displayName = Text.of("&a-管理ID-&7-:-&f %s").apply(like.id);
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.EDIT_DESCRIPTION);
					i.displayName = Text.color("&a-表示内容の編集");
				});
				
				s.onClick(e -> {
					Main.instance().descriptionEditors.put(p.getUniqueId(), like.id);
					p.closeInventory();
					Text.of("&a-新しい表示内容をチャット欄に入力して下さい。").accept(p::sendMessage);
				});
			}, 6);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.PROCEED_TO_CONFIRMATION_PAGE_OF_DELETING_LIKE);
					i.displayName = Text.color("&c-Likeの削除");
				});
				
				s.onClick(e -> new DeletingLikeConfirmationUI(like).open(p));
			}, 7);
		});
	}
	
}
