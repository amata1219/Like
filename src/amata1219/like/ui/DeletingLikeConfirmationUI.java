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
			l.title = "Deleting Like Confirmation";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.LIKE);
					i.displayName = Text.of("%a-Like").colored();
					i.lore(
						Text.of("&7-: &f-ID &7-@ &a-%s").format(like.id).colored(),
						Text.of("&7-: &f-お気に入り数 &7-@ &a-%s").format(like.favorites()).colored(),
						Text.of("&7-: &f-作成日時 &7-@ &a-%s").format(like.creationTimestamp()).colored()
					);
				});
			}, 2);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.DELETE_LIKE);
					i.displayName = Text.of("&c-このLikeを削除する！").colored();
					i.lore(
						Text.of("&7-※削除すると二度と復元出来なくなります。").colored()
					);
				});
				
				s.onClick(e -> {
					Main plugin = Main.instance();
					if(like.equals(plugin.descriptionEditors.get(p))) plugin.descriptionEditors.remove(p);
					plugin.players.get(p).myLikes.remove(like);
					Main.instance().deleteLike(like);
					Text.of("&c-Like(%s)を完全に削除しました。").format(like.id).sendTo(p);
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CANCEL_LIKE_DELETION);
					i.displayName = Text.of("&a-削除しないで元の画面に戻る！").colored();
				});
				
				s.onClick(e -> new LikeEditingUI(like).open(p));
			}, 6);
		});
	}
	
	
	/* 012345678
	 *   @ d c
	 */

}
