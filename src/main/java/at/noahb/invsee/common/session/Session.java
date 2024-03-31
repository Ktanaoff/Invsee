package at.noahb.invsee.common.session;

import at.noahb.invsee.InvseePlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public interface Session {
    UUID getUuid();

    void updatePlayerInventory();

    void updateSpectatorInventory();

    default void addSubscriber(UUID subscriber) {
        if (subscriber == null) return;
        if (hasSubscriber(subscriber)) return;
        Player player = InvseePlugin.getInstance().getServer().getPlayer(subscriber);
        if (player == null) return;

        Player other = InvseePlugin.getInstance().getServer().getPlayer(getUuid());
        if (other == null) return;

        getSubscribers().add(subscriber);
        player.getScheduler().run(InvseePlugin.getInstance(), scheduledTask -> player.openInventory(getInventory()), null);
    }

    Inventory getInventory();

    Set<UUID> getSubscribers();

    void removeSubscriber(UUID subscriber);

    boolean hasSubscriber(UUID subscriber);

    ReentrantLock getLock();

    default void update(Runnable runnable) {
        System.out.println("update");

        try {
            getLock().lock();
            runnable.run();
        } finally {
            if (getLock().isHeldByCurrentThread()) getLock().unlock();
        }
    }
}
