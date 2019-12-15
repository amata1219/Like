package amata1219.like.ui;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
import at.pcgamingfreaks.UUIDConverter;

public class TeleportationConfirmationUI implements InventoryUI {
	
	private final Main plugin = Main.instance();
	private final MainConfig config = plugin.config();
	private final Like like;
	private final InventoryUI previous;
	
	public TeleportationConfirmationUI(Like like, InventoryUI previous){
		this.like = like;
		this.previous = previous;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x2, (p, l) -> {
			l.title = "テレポートと招待の実行確認画面";
			
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
						Text.of("&7-%s").format(like.description()),
						"",
						Text.of("&7-作成者: &a-%s").format(UUIDConverter.getNameFromUUID(like.owner())),
						Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
						Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
						Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world()).or(() -> "Unknown")),
						Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z())
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
			}, 4);
			
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
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CANCEL_LIKE_TELEPORTATION);
					i.displayName = Text.color("&c-前のページに戻る！");
				});
				
				s.onClick(e -> previous.open(p));
			}, 7);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.OWNERS_OTHER_LIKES);
					i.displayName = Text.color("&a-この作者の他のLike情報");
				});
			}, 9);
			
			AtomicInteger slotIndex = new AtomicInteger(10);
			plugin.likes(like.owner()).stream()
			.filter(like -> like != this.like)
			.sorted(Comparator.comparing(Like::favorites).reversed())
			.limit(8)
			.forEach(like -> {
				l.put(s -> {
					s.icon(i -> {
						i.material = config.material(IconType.LIKE);
						i.displayName = " ";
						i.lore(
							Text.of("&7-%s").format(like.description()),
							"",
							Text.of("&7-作成者: &a-%s").format(UUIDConverter.getNameFromUUID(like.owner())),
							Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
							Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
							Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world()).or(() -> "Unknown")),
							Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z()),
							"",
							Text.color("&7-クリック: &a-下記機能の実行確認画面に移行します！"),
							Text.of("&7-: &a-このLikeにテレポートする！(&n-%sMP-&r&a)").format(config.teleportationCosts()),
							Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！(&n-%sMP-&r&a)").format(config.radiusOfInvitationScope(), config.invitationCosts())
						);
					});
					
					s.onClick(e -> new TeleportationConfirmationUI(like, this).open(p));
				}, slotIndex.getAndIncrement());
			});
		});
	}

}
