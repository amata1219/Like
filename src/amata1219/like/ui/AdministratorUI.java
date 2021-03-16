package amata1219.like.ui;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.item.Skull;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;

public class AdministratorUI implements InventoryUI {
	
	private final Main plugin = Main.plugin();
	private final MainConfig config = plugin.config();
	private final Like like;
	
	public AdministratorUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x3, (p, l) -> {
			l.title = "Likeの情報(管理者用)";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.basedItemStack = Skull.createFrom(like.owner());
					i.displayName = Text.of("&a-%s").format(like.ownerName());
				});
			}, 0);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.FAVORITES);
					i.displayName = Text.of("&a-お気に入りの数-&7-:-&f %s").format(like.favorites());
					i.amount = Math.min(Math.max(like.favorites(), 1), 64);
				});
			}, 3);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CREATION_TIMESTAMP);
					i.displayName = Text.of("&a-作成日時-&7-:-&f %s").format(like.creationTimestamp());
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.ID);
					i.displayName = Text.of("&a-ID-&7-:-&f %s").format(like.id);
					i.lore(Text.color("&7-クリックするとチャット欄にIDを表示します。"));
				});
				
				s.onClick(e -> Text.of("&7-ID > %s").apply(like.id).sendTo(p));
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.UNFAVORITE);
					i.displayName = Text.color("&c-お気に入りの解除");
				});
				
				s.onClick(e -> {
					like.decrementFavorites();
					plugin.players.get(p.getUniqueId()).unfavoriteLike(like);
					p.closeInventory();
					Text.of("&c-お気に入りを解除しました。").sendTo(p);
				});
			}, 6);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.EDIT_DESCRIPTION);
					i.displayName = Text.color("&a-表示内容の編集");
				});
				
				s.onClick(e -> {
					Main.plugin().descriptionEditors.put(p.getUniqueId(), like.id);
					p.closeInventory();
					Text.of(
						"&a-新しい表示内容をチャット欄に入力して下さい。",
						"cancelと入力した場合はキャンセルされます。"
					).sendTo(p);
				});
			}, 7);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.GO_TO_LIKE_DELETION_PAGE);
					i.displayName = Text.color("&c-Likeの削除");
				});
				
				s.onClick(e -> new DeletingLikeConfirmationUI(like).open(p));
			}, 8);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.OWNERS_OTHER_LIKES);
					i.displayName = Text.color("&a-この作者の他のLike情報");
				});
			}, 18);
			
			AtomicInteger slotIndex = new AtomicInteger(19);
			plugin.players.get(like.owner()).likes.values().stream()
			.filter(like -> like != this.like)
			.sorted(Comparator.comparing(Like::favorites).reversed())
			.limit(8)
			.forEach(like -> {
				l.put(s -> {
					s.icon(i -> {
						i.material = config.material(IconType.LIKE);
						i.displayName = Text.of("&a&l-%s").format(like.id);
						i.amount = Math.min(Math.max(like.favorites(), 1), 64);
						i.lore(
							Text.of("&7-%s").format(like.description()),
							"",
							Text.of("&7-作成者: &a-%s").format(like.ownerName()),
							Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
							Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
							Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world())),
							Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z()),
							"",
							Text.color("&7-クリック: 下記機能の実行確認画面に移行します！"),
							Text.of("&7-: &a-このLikeにテレポートする！ (%sMP)").format(config.teleportationCosts()),
							Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！ (%sMP)").format(config.radiusOfInvitationScope(), config.invitationCosts())
						);
					});
					
					s.onClick(e -> new TeleportationConfirmationUI(like, this).open(p));
				}, slotIndex.getAndIncrement());
			});
		});
	}

}
