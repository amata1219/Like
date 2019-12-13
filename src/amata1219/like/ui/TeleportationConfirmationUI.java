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

public class TeleportationConfirmationUI implements InventoryUI {
	
	private final MainConfig config = Main.instance().config();
	private final Like like;
	private final InventoryUI previous;
	
	public TeleportationConfirmationUI(Like like, InventoryUI previous){
		this.like = like;
		this.previous = previous;
	}
	
	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "Likeにテレポート";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.LIKE);
					i.displayName = " ";
					i.lore(
						Text.of("&a-ワールド-&7-: &f-%s").format(config.worldAlias(like.world()).or(() -> "Unknown")),
						Text.of("&a-座標-&7-: &f-X-&7-: &f-%s Y-&7-: &f-%s Z-&7-: &f-%s").format(like.x(), like.y(), like.z()),
						Text.of("&a-お気に入り数-&7-: &f-%s").format(like.favorites())
					);
				});
			}, 1);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.TELEPORT_TO_LIKE);
					i.displayName = Text.of("&a-このLikeにテレポートする！ (%sMP)").format(config.teleportationCosts());
				});
				
				s.onClick(e -> {
					p.teleport(like.hologram.getLocation());
					config.teleportationText().apply(like).accept(p::sendMessage);
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CANCEL_LIKE_TELEPORTATION);
					i.displayName = Text.color("&c-前のページに戻る！");
				});
				
				s.onClick(e -> previous.open(p));
			}, 7);
		});
	}

}
