package amata1219.like.masquerade.dsl;

import java.util.function.Function;

import amata1219.like.sound.SoundEffects;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.effect.BiEffect;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.option.Option;

public interface InventoryUI {

	Function<Player, Layout> layout();

	default Function<Player, Layout> build(Option option, BiEffect<Player, Layout> effect){
		return player -> effect.apply(player, new Layout(option));
	}

	default Function<Player, Layout> build(int size, BiEffect<Player, Layout> effect){
		return build(new Option(size), effect);
	}

	default Function<Player, Layout> build(Lines lines, BiEffect<Player, Layout> effect){
		return build(lines.size(), effect);
	}

	default Function<Player, Layout> build(InventoryType type, BiEffect<Player, Layout> effect){
		return build(new Option(type), effect);
	}

	default void open(Player player){
		player.openInventory(layout().apply(player).buildInventory());
		SoundEffects.OPEN_INVENTORY_UI.play(player);
	}

	default void playSound(Player player, Sound sound, float volume, float pitch){
		player.playSound(player.getLocation(), sound, volume, pitch);
	}

}
