package amata1219.niflheimr.dsl.component.format;

import amata1219.niflheimr.dsl.InventoryLayout;
import org.bukkit.inventory.Inventory;

public interface InventoryFormat {

    Inventory createInventoryWith(InventoryLayout layout);

    Inventory createInventoryWith(InventoryLayout layout, String title);

}
