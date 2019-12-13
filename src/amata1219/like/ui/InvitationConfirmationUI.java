package amata1219.like.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.config.MainConfig.InvitationText;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;

public class InvitationConfirmationUI implements InventoryUI {
	
	private final MainConfig config = Main.instance().config();
	private final Like like;
	private final InventoryUI previous;
	
	public InvitationConfirmationUI(Like like, InventoryUI previous){
		this.like = like;
		this.previous = previous;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "テレポートと招待の確認画面";
			
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
					i.material = config.material(IconType.GO_TO_LIKE_TELEPORTATION_OR_LIKE_INVITATION_CONFIRMATION_PAGE);
					i.displayName = Text.of("このLikeに近くのプレイヤーを招待する！ (%sMP)").format(config.invitationCosts());
				});
				
				s.onClick(e -> {
					final int radius = config.radiusOfInvitationScope();
					List<Player> playersNearby = p.getNearbyEntities(radius, radius, radius).stream()
							.filter(entity -> entity.getType() == EntityType.PLAYER)
							.map(entity -> (Player) entity)
							.collect(Collectors.toList());
					
					p.closeInventory();
					
					if(playersNearby.isEmpty()){
						Text.of("&c-近くに誰もいないため招待出来ませんでした。").accept(p::sendMessage);
						Text.of("&7-※MPは消費されていません。").accept(p::sendMessage);
						return;
					}
					
					InvitationText text = config.invitationText().apply(p, like);
					playersNearby.forEach(invitee -> text.clone().apply(invitee).accept(invitee::sendMessage));
					
					Text.of("&a-%s人のプレイヤーを招待しました。").apply(playersNearby.size()).accept(p::sendMessage);
				});
			}, 4);
			
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
