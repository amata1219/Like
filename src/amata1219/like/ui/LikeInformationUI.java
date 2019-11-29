package amata1219.like.ui;

import java.util.function.Function;

import org.bukkit.entity.Player;

import amata1219.masquerade.dsl.InventoryUI;
import amata1219.masquerade.dsl.component.Layout;
import amata1219.masquerade.option.Lines;

public class LikeInformationUI implements InventoryUI {

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x2, (p, l) -> {
			
		});
	}

}
