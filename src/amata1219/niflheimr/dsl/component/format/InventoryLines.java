package amata1219.niflheimr.dsl.component.format;

import amata1219.niflheimr.dsl.InventoryLayout;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public enum InventoryLines implements InventoryFormat {

    x1, x2, x3, x4, x5, x6;

    private final int size = (ordinal() + 1) * 9;

    @Override
    public Inventory createInventoryWith(InventoryLayout layout) {
        return Bukkit.createInventory(layout, size);
    }

    @Override
    public Inventory createInventoryWith(InventoryLayout layout, String title) {
        return Bukkit.createInventory(layout, size, title);
    }

}
