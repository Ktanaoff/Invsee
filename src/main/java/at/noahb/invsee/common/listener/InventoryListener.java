package at.noahb.invsee.common.listener;

import at.noahb.invsee.InvseePlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public record InventoryListener(InvseePlugin instance) implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        instance.getInvseeSessionManager().removeSubscriberFromSession(event.getPlayer());
        instance.getEnderseeSessionManager().removeSubscriberFromSession(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (InventoryAction.NOTHING.equals(event.getAction())) {
            return;
        }

        handle(event.getWhoClicked());
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        handle(event.getEntity());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        handle(event.getWhoClicked());
    }

    private void handle(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }

        instance.getInvseeSessionManager().updateContent(player);
        instance.getEnderseeSessionManager().updateContent(player);
    }

}
