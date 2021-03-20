package amata1219.like.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.event.ClickEvent;
import amata1219.like.masquerade.event.CloseEvent;
import amata1219.like.masquerade.event.OpenEvent;

public class UIListener implements Listener {

	@EventHandler
	public void onOpen(InventoryOpenEvent event){
		Layout layout = tryExtractLayout(event.getInventory());
		if (layout != null) layout.fire(new OpenEvent(event));
	}

	@EventHandler
	public void onClick(InventoryClickEvent event){
		Layout layout = tryExtractLayout(event.getInventory());
		if (layout == null) return;

		ClickEvent clickEvent = new ClickEvent(event);
		layout.fire(clickEvent);
		layout.slotAt(event.getSlot()).fire(clickEvent);

		event.setCancelled(true);
	}

	@EventHandler
	public void onOpen(InventoryCloseEvent event){
		Layout layout = tryExtractLayout(event.getInventory());
		if (layout != null) layout.fire(new CloseEvent(event));
	}

	private Layout tryExtractLayout(Inventory inventory) {
		return inventory.getHolder() instanceof Layout ?  (Layout) inventory.getHolder() : null;
	}

}
