package at.noahb.invsee.common.listener;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.invsee.session.InvseeSession;
import com.destroystokyo.paper.MaterialTags;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public record InventoryListener(InvseePlugin instance) implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        instance.getInvseeSessionManager().removeSubscriberFromSession(event.getPlayer());
        instance.getEnderseeSessionManager().removeSubscriberFromSession(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean setEmpty = false;
        if (InvseeSession.Placeholders.contains(event.getCurrentItem())) {
            if (MaterialTags.ARMOR.isTagged(event.getCursor()) || InvseeSession.Placeholders.isOffHandPlaceholder(event.getCurrentItem())) {
                setEmpty = true;
            } else {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().getSize() == 45 && event.getSlot() >= 36 && event.getSlot() < 40) {
            if (!MaterialTags.ARMOR.isTagged(event.getCursor()) && !event.getCursor().isEmpty()) {
                event.setCancelled(true);
                return;
            }

            if (!InvseeSession.ArmorSlot.values()[39 - event.getSlot()].checkIfItemFitsSlot(event.getCursor()) && !event.getCursor().isEmpty()) {
                event.setCancelled(true);
                return;
            }
        }

        if (InventoryAction.NOTHING == event.getAction()) {
            return;
        }

        if (setEmpty) event.setCurrentItem(ItemStack.empty());
        handle(event.getWhoClicked());
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        handle(event.getEntity());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (InvseeSession.Placeholders.contains(event.getOldCursor())) {
            event.setCancelled(true);
            return;
        }
        handle(event.getWhoClicked());
    }

    private void handle(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        player.getScheduler().run(instance, scheduledTask -> instance.getInvseeSessionManager().updateContent(player), null);
        player.getScheduler().run(instance, scheduledTask -> instance.getEnderseeSessionManager().updateContent(player), null);
    }
}
