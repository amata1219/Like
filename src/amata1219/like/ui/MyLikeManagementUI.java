package amata1219.like.ui;

import java.util.ArrayList;
import java.util.Collections;
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
	 * @---@---@
	 * 45, 49, 53
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
			l.title = Text.of("My Like Management @ %s").format(index + 1).colored();
			
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
							Text.of("&7-作成者: &a-%s").format(UUIDConverter.getNameFromUUID(like.owner())).colored(),
							Text.of("&7-お気に入り数: &a-%s").format(like.favorites()).colored(),
							Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()).colored(),
							Text.of("&7-ワールド: &a-%s").format(config.alias(like.world()).or(() -> "Unknown")).colored(),
							Text.of("&a-座標-&7-: &f-X-&7-: &f-%s Y-&7-: &f-%s Z-&7-: &f-%s").format(like.x(), like.y(), like.z()).colored(),
							"",
							Text.of("&7-クリック: &a-下記の機能の選択画面に移行します！").colored(),
							Text.of("&7-: &a-このLikeにテレポートする！(&n-%sMP-&r&a)").format(config.teleportationCosts()).colored(),
							Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！(&n-%sMP-&r&a)").format(config.radiusOfInvitationScope(), config.invitationCosts()).colored()
						);
					});
					s.onClick(e -> new TeleportationTypeSelectionUI(like).open(p));
				}, slotIndex);
			});
			
			if(index > 0){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
						i.displayName = Text.of("&a-前のページに戻る").colored();
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
			
			if(index < likes.size() / 45 + (likes.size() % 45 == 0 ? -1 : 0)){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
						i.displayName = Text.of("&a-次のページに進む").colored();
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
			Comparator<Like> comparator = Comparator.comparing(function);
			this.comparator = ordinal() % 2 == 0 ? comparator.reversed() : comparator;
		}
		
		private Order(Comparator<Like> comparator){
			this.comparator = comparator;
		}
		
	}

}
