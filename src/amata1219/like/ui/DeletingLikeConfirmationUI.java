package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;

public class DeletingLikeConfirmationUI implements InventoryUI {
	
	private final MainConfig config = Main.plugin().config();
	private final Like like;
	
	public DeletingLikeConfirmationUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "Likeの削除";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.LIKE);
					i.displayName = Text.of("&a&l-%s").format(like.id);
					i.amount = Math.min(Math.max(like.favorites(), 1), 64);
					i.lore(
						Text.of("&7-: &f-お気に入り数 &7-@ &a-%s").format(like.favorites()),
						Text.of("&7-: &f-作成日時 &7-@ &a-%s").format(like.creationTimestamp())
					);
				});
			}, 2);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.DELETE_LIKE);
					i.displayName = Text.color("&c-このLikeを削除する！");
					i.lore(
						Text.of("&7-※削除すると二度と復元出来なくなります。")
					);
				});
				
				s.onClick(e -> {
					like.delete(true);
					p.sendMessage(Text.of("&c-Like(%s)を完全に削除しました。").format(like.id));
					p.closeInventory();
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CANCEL_LIKE_DELETION);
					i.displayName = Text.color("&a-削除しないで元の画面に戻る！");
				});
				
				s.onClick(e -> new LikeEditingUI(like).open(p));
			}, 6);
		});
	}

}
