package amata1219.like.ui;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.Order;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.text.Text;

public class BookmarkUI extends AbstractMultipleUI {
	
	private final Bookmark bookmark;
	
	public BookmarkUI(Bookmark bookmark){
		this.bookmark = bookmark;
	}

	@Override
	protected List<Like> likes() {
		List<Like> likes = bookmark.likes();
		if(bookmark.order() == Order.NEWEST) Collections.reverse(likes);
		return likes;
	}

	@Override
	protected void layout(Player player, Layout l, List<Like> likes) {
		l.title = Text.of("%s @ %sページ目").format(bookmark.name, index + 1);
		
		int remainder = likes.size() - (index * 45) >= 45 ? 45 : likes.size() % 45;
		IntStream.range(0, remainder != 0 ? remainder : likes.isEmpty() ? 0 : 45).forEach(slotIndex -> {
			l.put(s -> {
				final Like like = likes.get(slotIndex);
				s.icon(i -> {
					i.material = config.material(IconType.LIKE);
					i.displayName = Text.of("&a&l-%s").format(like.id);
					i.amount = Math.min(Math.max(like.favorites(), 1), 64);
					i.lore(
						Text.of("&7-%s").format(like.description()),
						"",
						Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
						Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
						Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world())),
						Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z()),
						"",
						Text.color("&7-クリック: 下記機能の実行確認画面に移行します！"),
						Text.of("&7-: &a-このLikeにテレポートする！ (%s" + config.unitOfCost() + ")").format(config.teleportationCosts()),
						Text.of("&7-: &a-半径%sm以内にいるプレイヤーをこのLikeに招待する！ (%s" + config.unitOfCost() + ")").format(config.radiusOfInvitationScope(), config.invitationCosts())
					);
				});
				
				s.onClick(e -> new InvitationConfirmationUI(like, this).open(player));
			}, slotIndex);
		});
	}

}
