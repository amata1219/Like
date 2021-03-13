package amata1219.niflheimr.event;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class InventoryUIOpenEvent {

    public final Inventory inventory;

    public InventoryUIOpenEvent(InventoryOpenEvent event) {
        inventory = event.getInventory();
    }

}
