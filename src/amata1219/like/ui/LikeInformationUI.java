package amata1219.like.ui;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;
import amata1219.masquerade.text.Text;
import at.pcgamingfreaks.UUIDConverter;

public class LikeInformationUI implements InventoryUI {

	private final MainConfig config = Main.instance().config();
	private final Like like;
	
	public LikeInformationUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x2, (p, l) -> {
			l.title = "Like Information";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = Material.PLAYER_HEAD;
					UUID owner = like.creator();
					String playerName = UUIDConverter.getNameFromUUID(owner);
					i.displayName = Text.of("&a-%s").format(playerName).colored();
					i.raw = item -> ((SkullMeta) item.getItemMeta()).setOwningPlayer(Bukkit.getOfflinePlayer(owner));
				});
			}, 0);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.FAVORITES);
					i.displayName = Text.of("&a-お気に入りの数-&7-:-&f %s").format(like.favorites()).colored();
					i.amount = Math.min(like.favorites(), 64);
				});
			}, 3);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CREATION_TIMESTAMP);
					i.displayName = Text.of("&a-作成日時-&7-:-&f %s").format(like.creationTimestamp()).colored();
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.ID);
					i.displayName = Text.of("&a-管理ID-&7-:-&f %s").format(like.id).colored();
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.UNFAVORITE);
					i.displayName = Text.of("&c-お気に入りの解除").colored();
				});
			}, 6);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.icon(IconType.CREATORS_OTHER_LIKES);
					i.displayName = Text.of("&a-この作者の他のLike情報").colored();
				});
			}, 9);
			
			AtomicInteger slotIndex = new AtomicInteger(10);
			Main.instance().playerLikes.get(like.creator()).stream()
			.filter(like -> like != this.like)
			.sorted(Comparator.comparing(Like::favorites).reversed())
			.limit(8)
			.forEach(like -> {
				l.put(s -> {
					s.icon(i -> {
						i.material = config.icon(IconType.LIKE);
						i.displayName = " ";
						i.lore(
							Text.of("&a-ワールド-&7-: &f-%s").format(config.alias(like.world()).or(() -> "Unknown")).colored(),
							Text.of("&a-座標-&7-: &f-X-&7-: &f-%s Y-&7-: &f-%s Z-&7-: &f-%s").format(like.x(), like.y(), like.z()).colored(),
							Text.of("&a-お気に入り数-&7-: &f-%s").format(like.favorites()).colored()
						);
					});
				}, slotIndex.getAndIncrement());
			});
		});
	}

}
