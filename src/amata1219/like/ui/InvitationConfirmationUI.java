package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;

public class InvitationConfirmationUI implements InventoryUI {
	
	private final MainConfig config = Main.instance().config();
	private final Like like;
	
	/*
	 * 
	 * t -@---@-@-
	 * 
	 * t or i -@--@@-@-
	 * 
	 * 
	 * 
	 */
	
	public InvitationConfirmationUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return null;
	}

}
