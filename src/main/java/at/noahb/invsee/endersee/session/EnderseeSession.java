package at.noahb.invsee.endersee.session;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static net.kyori.adventure.text.Component.text;

public class EnderseeSession implements Session {

    private final UUID uuid;

    private final Set<UUID> subscribers;

    private final Inventory enderchest;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ReentrantLock lock = new ReentrantLock();

    public EnderseeSession(OfflinePlayer offlinePlayer) {
        this.uuid = offlinePlayer.getUniqueId();
        this.subscribers = new HashSet<>();

        if (offlinePlayer instanceof Player player) {
            this.enderchest = Bukkit.createInventory(player, InventoryType.ENDER_CHEST, player.name().append(text("'s enderchest")));
        } else {
            String name = offlinePlayer.getName() == null ? "unknown" : offlinePlayer.getName();
            this.enderchest = InvseePlugin.getInstance().getServer().createInventory(null, 45, text(name).append(text("'s enderchest")));
        }

        updateSpectatorInventory();
    }

    public EnderseeSession(OfflinePlayer player, UUID subscriber) {
        this(player);
        addSubscriber(subscriber);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void updatePlayerInventory() {
        OfflinePlayer offlinePlayer = InvseePlugin.getInstance().getServer().getOfflinePlayer(uuid);

        if (offlinePlayer instanceof Player player) {
            if (!lock.tryLock()) {
                executorService.submit(this::updatePlayerInventory);
            }
            Inventory enderChest = player.getEnderChest();
            for (int i = 0; i < enderChest.getSize(); i++) {
                enderChest.setItem(i, this.enderchest.getItem(i));
            }

            lock.unlock();
        }
    }

    @Override
    public void updateSpectatorInventory() {
        if (!lock.tryLock()) {
            executorService.submit(this::updateSpectatorInventory);
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer instanceof Player player) {
            ItemStack[] playerInv = player.getEnderChest().getContents();
            for (int i = 0; i < playerInv.length; i++) {
                enderchest.setItem(i, playerInv[i]);
            }
        }
        lock.unlock();
    }

    @Override
    public void addSubscriber(UUID subscriber) {
        if (subscriber == null) return;
        if (subscribers.contains(subscriber)) return;
        Player player = InvseePlugin.getInstance().getServer().getPlayer(subscriber);
        if (player == null) return;

        Player other = InvseePlugin.getInstance().getServer().getPlayer(uuid);
        if (other == null) return;

        subscribers.add(subscriber);
        player.getScheduler().run(InvseePlugin.getInstance(), scheduledTask -> player.openInventory(enderchest), null);
    }

    @Override
    public Set<UUID> getSubscribers() {
        return subscribers;
    }

    @Override
    public void removeSubscriber(UUID subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public boolean hasSubscriber(UUID subscriber) {
        return subscribers.contains(subscriber);
    }
}
