package amata1219.niflheimr.dsl.component.format;

import amata1219.niflheimr.dsl.InventoryLayout;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryType implements InventoryFormat {

    public final org.bukkit.event.inventory.InventoryType type;

    public InventoryType(org.bukkit.event.inventory.InventoryType type) {
        this.type = type;
    }

    @Override
    public Inventory createInventoryWith(InventoryLayout layout) {
        return Bukkit.createInventory(layout, type);
    }

    @Override
    public Inventory createInventoryWith(InventoryLayout layout, String title) {
        return Bukkit.createInventory(layout, type, title);
    }

}
