package at.noahb.invsee;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SessionManager {

    private final Set<Session> currentlySpectating = new HashSet<>();
    private final Invsee instance;

    public SessionManager(Invsee instance) {
        this.instance = instance;
    }

    public Set<UUID> getSubscribers(OfflinePlayer player) {
        return currentlySpectating.stream().filter(session -> player.getUniqueId().equals(session.getUuid())).findFirst().orElse(new Session(player)).getSubscribers();
    }

    public void addSubscriberToSession(OfflinePlayer player, UUID subscriber) {
        currentlySpectating.stream()
                .filter(filterSession -> player.getUniqueId().equals(filterSession.getUuid()))
                .findFirst()
                .ifPresentOrElse(session -> session.addSubscriber(subscriber), () -> currentlySpectating.add(new Session(player, subscriber)));
    }

    public void removeSubscriberFromSession(@NotNull HumanEntity subscriber) {
        Optional<Session> first = currentlySpectating.stream().filter(session -> session.getSubscribers().contains(subscriber.getUniqueId())).findFirst();

        first.ifPresent(session -> {
            session.removeSubscriber(subscriber.getUniqueId());
            if (session.getSubscribers().isEmpty()) {
                currentlySpectating.remove(session);
            }
            subscriber.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        });

    }
}
