package amata1219.niflheimr.dsl.component.slot;

import amata1219.niflheimr.constant.Constants;
import amata1219.niflheimr.dsl.component.Icon;
import amata1219.niflheimr.event.InventoryUICloseEvent;
import amata1219.niflheimr.event.InventoryUIOpenEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AnimatedSlot extends Slot {

    protected ArrayList<Consumer<Icon>> differences = new ArrayList<>();
    private Consumer<InventoryUIOpenEvent> actionOnOpen = Constants.noOperation();
    private Consumer<InventoryUICloseEvent> actionOnClose = Constants.noOperation();

    public AnimatedSlot append(Consumer<Icon> difference) {
        differences.add(difference);
        return this;
    }

    public Consumer<InventoryUIOpenEvent>  actionOnOpen() {
        return actionOnOpen;
    }

    public void onOpen(Consumer<InventoryUIOpenEvent> actionOnOpen) {
        this.actionOnOpen = actionOnOpen;
    }

    public Consumer<InventoryUICloseEvent>  actionOnClose() {
        return actionOnClose;
    }

    public void oClose(Consumer<InventoryUICloseEvent> actionOnClose) {
        this.actionOnClose = actionOnClose;
    }

}
