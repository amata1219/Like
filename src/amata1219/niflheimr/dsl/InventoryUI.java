package amata1219.niflheimr.dsl;

import amata1219.niflheimr.Niflheimr;
import amata1219.niflheimr.dsl.component.format.InventoryFormat;
import amata1219.niflheimr.dsl.component.format.InventoryLines;
import amata1219.niflheimr.dsl.component.format.InventoryType;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface InventoryUI {

    InventoryLayout layout(Player viewer);

    default InventoryLayout build(InventoryFormat format, Consumer<InventoryLayout> settings) {
        InventoryLayout layout = new InventoryLayout(format);
        settings.accept(layout);
        return layout;
    }

    default InventoryLayout build(InventoryLines lines, Consumer<InventoryLayout> settings) {
        return build((InventoryFormat) lines, settings);
    }

    default InventoryLayout build(org.bukkit.event.inventory.InventoryType type, Consumer<InventoryLayout> settings) {
        return build(new InventoryType(type), settings);
    }

    default void openInventory(Player player) {
        player.openInventory(layout(player).buildInventory());
    }

    default void openInventoryAsynchronously(Player player) {
        Niflheimr plugin = Niflheimr.instance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> openInventory(player));
    }

}
