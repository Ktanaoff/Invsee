package at.noahb.invsee.listener;

import at.noahb.invsee.Invsee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public record InventoryListener(Invsee instance) implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        instance.getSessionManager().removeSubscriberFromSession(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.NOTHING) {
            return;
        }
        System.out.println("InvClick");
        handle(event.getWhoClicked());
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        System.out.println("pickup");
        handle(event.getEntity());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        System.out.println("drag");
        handle(event.getWhoClicked());
    }

    private void handle(LivingEntity entity) {
        if (entity instanceof Player player) {

            player.getScheduler().run(instance, scheduledTask -> instance.getSessionManager().updateContent(player), null);
        }
    }
}
