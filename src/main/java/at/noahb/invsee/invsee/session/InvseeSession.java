package at.noahb.invsee.invsee.session;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.session.Session;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static net.kyori.adventure.text.Component.text;


public class InvseeSession implements Session {

    private final UUID uuid;
    private final Set<UUID> subscribers;
    private final Inventory inventory;
    private final ReentrantLock lock = new ReentrantLock();
    private final Cache<UUID, Player> playerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    public InvseeSession(OfflinePlayer offlinePlayer) {
        this.uuid = offlinePlayer.getUniqueId();
        this.subscribers = new HashSet<>();

        if (offlinePlayer instanceof Player player) {
            this.inventory = Bukkit.createInventory(player, 45, player.name().append(text("'s inventory")));
        } else {
            String name = offlinePlayer.getName() == null ? "unknown" : offlinePlayer.getName();
            this.inventory = InvseePlugin.getInstance().getServer().createInventory(null, 45, text(name).append(text("'s inventory")));
        }

        updateSpectatorInventory();
    }

    public InvseeSession(OfflinePlayer player, UUID subscriber) {
        this(player);
        addSubscriber(subscriber);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Set<UUID> getSubscribers() {
        return subscribers;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private PlayerInventory getPlayerInventory(OfflinePlayer offlinePlayer) {
        if (offlinePlayer instanceof Player player) {
            return player.getInventory();
        }

        Optional<Player> player = getPlayerOffline(offlinePlayer);

        return player.map(Player::getInventory).orElse(null);
    }

    @Override
    public void removeSubscriber(UUID subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void updateSpectatorInventory() {
        update(() -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            PlayerInventory playerInv = getPlayerInventory(offlinePlayer);
            if (playerInv == null) {
                return;
            }
            System.out.println(playerInv.getSize());
            for (int i = 0; i < 41; i++) {
                inventory.setItem(i, playerInv.getItem(i));
            }
        });
    }

    @Override
    public boolean hasSubscriber(UUID uuid) {
        return subscribers.contains(uuid);
    }

    @Override
    public void updatePlayerInventory() {
        update(() -> {
            OfflinePlayer offlinePlayer = InvseePlugin.getInstance().getServer().getOfflinePlayer(uuid);
            PlayerInventory playerInventory = getPlayerInventory(offlinePlayer);
            if (playerInventory == null) {
                return;
            }
            System.out.println(playerInventory.getSize());
            for (int i = 0; i <= playerInventory.getSize(); i++) {
                playerInventory.setItem(i, this.inventory.getItem(i));
            }
        });
    }

    @Override
    public ReentrantLock getLock() {
        return lock;
    }

    @Override
    public void cache(Player player) {
        playerCache.put(this.uuid, player);
    }

    @Override
    public Player getCachedPlayer() {
        return playerCache.getIfPresent(this.uuid);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        InvseeSession session = (InvseeSession) object;
        return Objects.equals(uuid, session.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}