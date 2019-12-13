package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;

public class TeleportationConfirmationUI implements InventoryUI {
	
	private final Like like;
	private final InventoryUI previous;
	
	public TeleportationConfirmationUI(Like like, InventoryUI previous){
		this.like = like;
		this.previous = previous;
	}


	/*
	 * 
	 * t -@---@-@-
	 * 
	 * t or i -@--@@-@-
	 * 
	 * 
	 * 
	 */
	
	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "Likeにテレポート";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
		});
	}

}
