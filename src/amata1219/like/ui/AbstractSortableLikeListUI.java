package amata1219.like.ui;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.text.Text;

public abstract class AbstractSortableLikeListUI extends AbstractMultipleUI {
	
	protected Order order = Order.CREATION_TIME_IN_DESCENDING;

	@Override
	protected void layout(Player player, Layout l, List<Like> likes) {
		likes.sort(order.comparator);
		
		if(order == Order.CREATION_TIME_IN_DESCENDING){
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_DESCENDING_ORDER);
					i.displayName = Text.color("&a-作成日が新しい順に表示されています！");
					i.gleam();
				});
			}, 47);
		}else{
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_DESCENDING_ORDER);
					i.displayName = Text.color("&7-作成日が新しい順に並び替える");
				});
				
				s.onClick(e -> {
					order = Order.CREATION_TIME_IN_DESCENDING;
					open(player);
				});
			}, 47);
		}
		
		if(order == Order.CREATION_TIME_IN_ASCENDING){
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER);
					i.displayName = Text.color("&a-作成日時が古い順に表示されています！");
					i.gleam();
				});
			}, 48);
		}else{
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER);
					i.displayName = Text.color("&7-作成日時が古い順に並び替える");
				});
				
				s.onClick(e -> {
					order = Order.CREATION_TIME_IN_ASCENDING;
					open(player);
				});
			}, 48);
		}
		
		if(order == Order.FAVORITES_IN_DESCENDING){
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_FAVORITES_IN_DESCENDING_ORDER);
					i.displayName = Text.color("&a-お気に入り数が多い順に表示されています！");
					i.gleam();
				});
			}, 50);
		}else{
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_FAVORITES_IN_DESCENDING_ORDER);
					i.displayName = Text.color("&7-お気に入り数が多い順に並び替える");
				});
				
				s.onClick(e -> {
					order = Order.FAVORITES_IN_DESCENDING;
					open(player);
				});
			}, 50);
		}
		
		if(order == Order.FAVORITES_IN_ASCENDING){
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_FAVORITES_IN_ASCENDING_ORDER);
					i.displayName = Text.color("&a-お気に入り数が少ない順に表示されています！");
					i.gleam();
				});
			}, 51);
		}else{
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.SORT_BY_FAVORITES_IN_ASCENDING_ORDER);
					i.displayName = Text.color("&7-お気に入り数が少ない順に並び替える");
				});
				
				s.onClick(e -> {
					order = Order.FAVORITES_IN_ASCENDING;
					open(player);
				});
			}, 51);
		}
	}
	
	protected enum Order {
		
		CREATION_TIME_IN_ASCENDING((l1, l2) -> Long.compare(l1.id, l2.id)),
		CREATION_TIME_IN_DESCENDING((l1, l2) -> Long.compare(l1.id, l2.id)),
		FAVORITES_IN_ASCENDING(Like::favorites),
		FAVORITES_IN_DESCENDING(Like::favorites);
		
		public final Comparator<Like> comparator;
		
		private Order(Comparator<Like> comparator){
			this.comparator = ordinal() % 2 == 0 ? comparator : comparator.reversed();
		}
		
		private Order(Function<Like, Integer> function){
			this(Comparator.comparing(function));
		}

	}
	
}
