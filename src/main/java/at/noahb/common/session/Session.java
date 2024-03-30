package at.noahb.common.session;

import java.util.Set;
import java.util.UUID;

public interface Session {
    UUID getUuid();

    void updatePlayerInventory();

    void updateSpectatorInventory();

    void addSubscriber(UUID subscriber);

    Set<UUID> getSubscribers();

    void removeSubscriber(UUID subscriber);

    boolean hasSubscriber(UUID subscriber);
}
