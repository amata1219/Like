package amata1219.like.ui;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import amata1219.like.Like;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.text.Text;

public class MyLikeManagementUI extends AbstractLikeListUI {
	
	public MyLikeManagementUI(UUID owner){
		super(owner);
	}

	@Override
	protected List<Like> likes() {
		return plugin.likes(owner);
	}

	@Override
	protected void layout(Player p, Layout l, List<Like> likes) {
		l.title = Text.of("自分のLike @ %s").format(index + 1);
		
		l.defaultSlot(s -> {
			s.icon(i -> {
				i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
				i.displayName = " ";
			});
		});
		
		final int start = index * 45;
		final int remainder = likes.size() % 45;
		IntStream.range(start, start + (remainder == 0 ? 45 : remainder)).forEach(slotIndex -> {
			l.put(s -> {
				final Like like = likes.get(slotIndex);
				s.icon(i -> {
					i.material = config.material(IconType.LIKE);
					i.displayName = " ";
					i.amount = Math.min(like.favorites(), 64);
					i.lore(
						Text.of("&7-%s").format(like.description()),
						"",
						Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
						Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
						Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world()).or(() -> "Unknown")),
						Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z()),
						"",
						Text.color("&7-クリック: &a-下記機能の実行確認画面に移行します！"),
						Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！(&n-%sMP-&r&a)").format(config.radiusOfInvitationScope(), config.invitationCosts())
					);
				});
				
				s.onClick(e -> new InvitationConfirmationUI(like, this).open(p));
			}, slotIndex);
		});
	}

}
