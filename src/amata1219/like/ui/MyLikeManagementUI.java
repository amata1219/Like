package amata1219.like.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import amata1219.masquerade.item.Skull;
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
	 * ---------
	 * @-@@-@@-@
	 * 45,
	 * 47,48
	 * 50,51
	 * 53
	 * 
	 */
	
	private final Main plugin = Main.instance();
	private final MainConfig config = plugin.config();
	private final UUID owner;
	private int index;
	private Order order = Order.CREATION_DATE_ASCENDING;
	
	public MyLikeManagementUI(UUID owner){
		this.owner = owner;
	}

	@Override
	public Function<Player, Layout> layout() {
		final ArrayList<Like> likes = new ArrayList<>(plugin.likes(owner));
		final Type where = Type.where(index, likes);
		likes.sort(order.comparator);
		return build(Math.min(likes.size() - (index * 45), 45), (p, l) -> {
			l.title = Text.of("My Likes @ %s").apply(index + 1).get();
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			int start = index * 45;
			int remainder = likes.size() % 45;
			IntStream.range(start, start + (remainder == 0 ? 45 : remainder)).forEach(slotIndex -> {
				l.put(s -> {
					Like like = likes.get(slotIndex);
					s.icon(i -> {
						i.material = config.icon(IconType.LIKE);
						i.displayName = " ";
						i.lore(
							Text.of("&7-%s").apply(like.description()),
							Text.empty(),
							Text.of("&7-作成者: &a-%s").apply(UUIDConverter.getNameFromUUID(like.owner())),
							Text.of("&7-お気に入り数: &a-%s").apply(like.favorites()),
							Text.of("&7-作成日時: &a-%s").apply(like.creationTimestamp()),
							Text.of("&7-ワールド: &a-%s").apply(config.alias(like.world()).or(() -> "Unknown")),
							Text.of("&a-座標-&7-: &f-X-&7-: &f-%s Y-&7-: &f-%s Z-&7-: &f-%s").apply(like.x(), like.y(), like.z()),
							Text.empty(),
							Text.of("&7-クリック: &a-下記の機能の選択画面に移行します！"),
							Text.of("&7-: &a-このLikeにテレポートする！(&n-%sMP-&r&a)").apply(config.teleportationCosts()),
							Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！(&n-%sMP-&r&a)").apply(config.radiusOfInvitationScope(), config.invitationCosts())
						);
					});
					
					s.onClick(e -> new TeleportationTypeSelectionUI(like).open(p));
				}, slotIndex);
			});
			
			if(where != Type.FIRST){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
						i.displayName = Text.of("&a-前のページに戻る").get();
					});
					
					s.onClick(e -> {
						index--;
						open(p);
					});
				}, 45);
			}
			
			l.put(s -> {
				s.icon(i -> {
					
				});
			}, 49);
			
			if(where != Type.LAST){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
						i.displayName = Text.of("&a-次のページに進む").get();
					});
					
					s.onClick(e -> {
						index++;
						open(p);
					});
				}, 53);
			}
			
		});
	}
	
	private enum Type {
		
		FIRST,
		MIDDLE,
		LAST;
		
		private static Type where(int index, List<Like> likes){
			if(index == 0 || likes.isEmpty()) return Type.FIRST;
			else if(index < likes.size() / 45 + (likes.size() % 45 == 0 ? -1 : 0)) return Type.MIDDLE;
			else return Type.LAST;
		}
		
	}
	
	private enum Order {
		
		FAVORITES_ASCENDING(Like::favorites),
		FAVORITES_DESCENDING(Like::favorites),
		CREATION_DATE_ASCENDING((l1, l2) -> Long.compare(l1.id, l2.id)),
		CREATION_DATE_DESCENDING((l1, l2) -> Long.compare(l1.id, l2.id));
		
		public final Comparator<Like> comparator;
		
		private Order(Function<Like, Integer> function){
			this(Comparator.comparing(function));
		}
		
		private Order(Comparator<Like> comparator){
			this.comparator = ordinal() % 2 == 0 ? comparator.reversed() : comparator;
		}
		
	}

}
