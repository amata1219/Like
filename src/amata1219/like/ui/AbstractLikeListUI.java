package amata1219.like.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.config.MainConfig.IconType;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.item.Skull;
import amata1219.masquerade.text.Text;

public abstract class AbstractLikeListUI extends AbstractMultipleUI {
	
	protected final UUID owner;
	
	protected AbstractLikeListUI(UUID owner){
		this.owner = owner;
	}
	
	@Override
	public Function<Player, Layout> layout() {
		final List<Like> likes = new ArrayList<>(likes());
		likes.sort(order.comparator);
		return build(Math.min(likes.size() - (index * 45), 45), (p, l) -> {
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			final Type type = Type.type(index, likes);
			if(type == Type.FIRST){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ==");
						i.displayName = Text.color("&c-これ以上前には戻れません");
					});
				}, 45);
			}else{
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
						i.displayName = Text.color("&a-前のページに戻る");
					});
					
					s.onClick(e -> {
						index--;
						open(p);
					});
				}, 45);
			}
			
			if(type == Type.LAST){
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19");
						i.displayName = Text.color("&c-これ以上次には進めません");
					});
				}, 53);
			}else{
				l.put(s -> {
					s.icon(i -> {
						i.basedItemStack = Skull.createFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
						i.displayName = Text.color("&a-次のページに進む");
					});
					
					s.onClick(e -> {
						index++;
						open(p);
					});
				}, 53);
			}
			
			if(order == Order.CREATION_DATE_IN_DESCENDING){
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
						order = Order.CREATION_DATE_IN_DESCENDING;
						open(p);
					});
				}, 47);
			}
			
			if(order == Order.CREATION_DATE_IN_ASCENDING){
				l.put(s -> {
					s.icon(i -> {
						i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER);
						i.displayName = Text.color("&a-作成日が古い順に表示されています！");
						i.gleam();
					});
				}, 48);
			}else{
				l.put(s -> {
					s.icon(i -> {
						i.material = config.material(IconType.SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER);
						i.displayName = Text.color("&7-作成日が古い順に並び替える");
					});
					
					s.onClick(e -> {
						order = Order.CREATION_DATE_IN_ASCENDING;
						open(p);
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
						open(p);
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
						open(p);
					});
				}, 51);
			}
			
			layout(p, l, likes);
		});
	}
	
	protected abstract List<Like> likes();
	
	protected abstract void layout(Player p, Layout l, List<Like> likes);
	
}
