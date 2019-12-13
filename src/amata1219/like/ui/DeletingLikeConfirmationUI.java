package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;

public class DeletingLikeConfirmationUI implements InventoryUI {
	
	private final MainConfig config = Main.instance().config();
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
					i.material = config.icon(IconType.LIKE);
					i.displayName = Text.color("%a-Like");
					i.lore(
						Text.of("&7-: &f-ID &7-@ &a-%s").apply(like.id),
						Text.of("&7-: &f-お気に入り数 &7-@ &a-%s").apply(like.favorites()),
						Text.of("&7-: &f-作成日時 &7-@ &a-%s").apply(like.creationTimestamp())
					);
				});
			}, 2);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.DELETE_LIKE);
					i.displayName = Text.color("&c-このLikeを削除する！");
					i.lore(
						Text.of("&7-※削除すると二度と復元出来なくなります。")
					);
				});
				
				s.onClick(e -> {
					Main.instance().deleteLike(like);
					p.sendMessage(Text.of("&c-Like(%s)を完全に削除しました。").apply(like.id));
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CANCEL_LIKE_DELETION);
					i.displayName = Text.color("&a-削除しないで元の画面に戻る！");
				});
				
				s.onClick(e -> new LikeEditingUI(like).open(p));
			}, 6);
		});
	}

}
