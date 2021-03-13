package amata1219.niflheimr.dsl;

import amata1219.niflheimr.dsl.component.Icon;
import amata1219.niflheimr.dsl.component.slot.AnimatedSlot;
import amata1219.niflheimr.dsl.component.slot.Slot;
import amata1219.niflheimr.event.InventoryUIClickEvent;
import amata1219.niflheimr.event.InventoryUICloseEvent;
import amata1219.niflheimr.event.InventoryUIOpenEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryOperationListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        InventoryLayout layout = tryExtractInventoryLayout(event.getInventory());
        if (layout == null) return;

        InventoryUIClickEvent ev = new InventoryUIClickEvent(event);
        layout.actionOnClick().accept(ev);

        Icon currentIcon = layout.currentIcons.get(ev.clickedSlot);
        if (currentIcon != null) currentIcon.actionOnClick().accept(ev.current);

        Slot currentSlot = layout.slotAt(ev.clickedSlot);
        if (currentSlot != null) currentSlot.actionOnClick().accept(ev);

        event.setCancelled(ev.shouldCancel || !layout.slotAt(event.getSlot()).editable);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        InventoryLayout layout = tryExtractInventoryLayout(event.getInventory());
        if (layout == null) return;

        InventoryUIOpenEvent ev = new InventoryUIOpenEvent(event);

        layout.actionOnOpen().accept(ev);
        for (AnimatedSlot slot : layout.animatedSlots.values()) slot.actionOnOpen().accept(ev);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryLayout layout = tryExtractInventoryLayout(event.getInventory());
        if (layout == null) return;

        InventoryUICloseEvent ev = new InventoryUICloseEvent(event);

        layout.actionOnClose().accept(ev);
        for (AnimatedSlot slot : layout.animatedSlots.values()) slot.actionOnClose().accept(ev);
    }

    private InventoryLayout tryExtractInventoryLayout(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof InventoryLayout ? (InventoryLayout) holder : null;
    }

}
