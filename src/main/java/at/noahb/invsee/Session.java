package at.noahb.invsee;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;


public class Session {

    private final UUID uuid;

    private final Set<UUID> subscribers;

    private final Inventory inventory;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ReentrantLock lock = new ReentrantLock();

    public Session(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.subscribers = new HashSet<>();

        //todo remove this if holder is not needed
        if (player instanceof Player onlinePlayer) {
            this.inventory = Bukkit.createInventory(onlinePlayer, 45);
        } else {
            this.inventory = Invsee.getInstance().getServer().createInventory(null, 45);
        }

        updateSpectatorInventory();
    }

    public Session(OfflinePlayer player, UUID subscriber) {
        this(player);
        addSubscriber(subscriber);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<UUID> getSubscribers() {
        return subscribers;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addSubscriber(UUID subscriber) {
        if (subscribers.contains(subscriber)) return;
        Player player = Invsee.getInstance().getServer().getPlayer(subscriber);
        if (player == null) return;

        Player other = Invsee.getInstance().getServer().getPlayer(uuid);
        if (other == null) return;

        subscribers.add(subscriber);
        player.getScheduler().run(Invsee.getInstance(), scheduledTask -> player.openInventory(inventory), null);
    }

    public void removeSubscriber(UUID subscriber) {
        subscribers.remove(subscriber);
    }

    public void updateSpectatorInventory() {
        if (!lock.tryLock()) {
            System.out.println("no lock spec");
            executorService.submit(this::updateSpectatorInventory);
            return;
        }
        System.out.println("lock spec");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer instanceof Player player) {
            ItemStack[] playerInv = player.getInventory().getContents();
            for (int i = 0; i < playerInv.length; i++) {
                inventory.setItem(i, playerInv[i]);
            }
            ItemStack[] armorContents = player.getInventory().getArmorContents();

            for (int i = 0; i < armorContents.length; i++) {
                inventory.setItem(36+i, armorContents[i]);
            }
        }
        lock.unlock();
    }

    public boolean hasSubscriber(UUID uuid) {
        return subscribers.contains(uuid);
    }

    public void updatePlayerInventory() {
        OfflinePlayer offlinePlayer = Invsee.getInstance().getServer().getOfflinePlayer(uuid);

        if (offlinePlayer instanceof Player player) {
            PlayerInventory playerInventory = player.getInventory();
            if (!lock.tryLock()) {
                System.out.println("No lock updatePlayerInv");
                executorService.submit(this::updatePlayerInventory);
            }
            System.out.println("lock player");
            for (int i = 0; i <= 40; i++) {
                playerInventory.setItem(i, inventory.getItem(i));
            }

            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Session session = (Session) object;
        return Objects.equals(uuid, session.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
