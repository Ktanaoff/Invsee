package at.noahb.invsee.listener;

import at.noahb.invsee.Invsee;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public record InventoryListener(Invsee instance) implements Listener {

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        instance.getSessionManager().removeSubscriberFromSession(event.getPlayer());
    }

    @EventHandler
    public void onInventory(PlayerInventorySlotChangeEvent event) {
        System.out.println("Abc");
        instance.getSessionManager().updateContent(event.getPlayer());
    }
}
