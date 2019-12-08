package amata1219.like.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.text.Text;
import at.pcgamingfreaks.UUIDConverter;

public class MyLikeManagementUI implements InventoryUI {
	
	/*
	 * 
	 * 表示数: 1ページ当たり45個
	 * 
	 * 昇順ソート/降順ソート
	 * 
	 * size - (p x 45)
	 * 
	 * Math.min(size - (p x 45), 45) % 9
	 * 
	 */
	
	private final static Comparator<Like> COMPARATOR = Comparator.comparing(Like::favorites);
	
	private final MainConfig config = Main.instance().config();
	private final UUID owner;
	private int index;
	private Order order;
	
	public MyLikeManagementUI(UUID owner){
		this.owner = owner;
	}

	@Override
	public Function<Player, Layout> layout() {
		ArrayList<Like> likes = new ArrayList<>(Main.instance().playerLikes.getOrDefault(owner, Collections.emptyList()));
		likes.sort(order == Order.ASCENDING ? COMPARATOR : COMPARATOR.reversed());
		return build(Math.min(likes.size() - (index * 45), 45), (p, l) -> {
			l.title = "My Like Management";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			int start = Math.min(index * 45, likes.size() - 1);
			IntStream.range(start, Math.min(start + 45, likes.size())).forEach(slotIndex -> {
				l.put(s -> {
					Like like = likes.get(slotIndex);
					s.icon(i -> {
						i.material = config.icon(IconType.LIKE);
						i.displayName = " ";
						i.lore(
							Text.of("&7-%s").format(like.description()).colored(),
							"",
							Text.of("&7-作成者: &a-%s").format(UUIDConverter.getNameFromUUID(like.creator())).colored(),
							Text.of("&7-お気に入り数: &a-%s").format(like.favorites()).colored(),
							Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()).colored(),
							Text.of("&7-ワールド: &a-%s").format(config.alias(like.world()).or(() -> "Unknown")).colored(),
							Text.of("&a-座標-&7-: &f-X-&7-: &f-%s Y-&7-: &f-%s Z-&7-: &f-%s").format(like.x(), like.y(), like.z()).colored(),
							"",
							Text.of("&7-左クリック: &a-このLikeにテレポートする！(&n-%sMP-&r&a)").format(config.teleportationCosts()).colored(),
							Text.of("&7-右クリック: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！(&n-%sMP-&r&a)").format(config.radiusOfInvitationScope(), config.invitationCosts()).colored()
						);
					});
				}, slotIndex);
			});
		});
	}
	
	/*
	 * ここは鯖内最多の蔵書数を誇る知識の樹海！
	 * 
	 * 作成者: amata1219
	 * お気に入り数: 64
	 * 作成日時: 2019/12/08 17:58:06
	 * ワールド: メイン
	 * 座標: X: 10 Y: 12 Z: 100
	 * 
	 * 左クリック: このLikeにテレポートする！(50.0MP)
	 * 右クリック: 半径32m以内にいるプレイヤーをこのLikeに招待する！(120.0MP)
	 */
	
	private enum Order {
		
		ASCENDING,
		DESCENDING;
		
	}

}
