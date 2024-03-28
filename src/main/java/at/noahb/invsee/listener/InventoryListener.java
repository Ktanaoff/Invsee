package at.noahb.invsee.listener;

import at.noahb.invsee.Invsee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public record InventoryListener(Invsee instance) implements Listener {

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        instance.getSessionManager().removeSubscriberFromSession(event.getPlayer());
    }
}
