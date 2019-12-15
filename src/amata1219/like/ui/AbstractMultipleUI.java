package amata1219.like.ui;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.masquerade.dsl.InventoryUI;

public abstract class AbstractMultipleUI implements InventoryUI {

	protected final Main plugin = Main.instance();
	protected final MainConfig config = plugin.config();
	protected int index;
	protected Order order = Order.CREATION_DATE_IN_ASCENDING;
	
	protected enum Type {
		
		FIRST,
		MIDDLE,
		LAST;
		
		protected static Type type(int index, List<Like> likes){
			if(index == 0 || likes.isEmpty()) return Type.FIRST;
			else if(index < likes.size() / 45 + (likes.size() % 45 == 0 ? -1 : 0)) return Type.MIDDLE;
			else return Type.LAST;
		}
		
	}
	
	protected enum Order {
		
		CREATION_DATE_IN_ASCENDING((l1, l2) -> Long.compare(l1.id, l2.id)),
		CREATION_DATE_IN_DESCENDING((l1, l2) -> Long.compare(l1.id, l2.id)),
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
