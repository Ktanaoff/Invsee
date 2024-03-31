package at.noahb.invsee.common.session;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public interface Session {
    UUID getUuid();

    void updatePlayerInventory();

    void updateSpectatorInventory();

    void addSubscriber(UUID subscriber);

    Set<UUID> getSubscribers();

    void removeSubscriber(UUID subscriber);

    boolean hasSubscriber(UUID subscriber);

    ReentrantLock getLock();

    default void update(Runnable runnable) {
        while (!getLock().tryLock()) {

        }
        try {
            runnable.run();
        } finally {
            if (getLock().isHeldByCurrentThread()) getLock().unlock();
        }
    }
}
