package at.noahb.invsee.session;

import at.noahb.common.InvseePlugin;
import at.noahb.common.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static net.kyori.adventure.text.Component.text;


public class InvseeSession implements Session {

    private final UUID uuid;

    private final Set<UUID> subscribers;

    private final Inventory inventory;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ReentrantLock lock = new ReentrantLock();

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
        if (subscriber == null) return;
        if (subscribers.contains(subscriber)) return;
        Player player = InvseePlugin.getInstance().getServer().getPlayer(subscriber);
        if (player == null) return;

        Player other = InvseePlugin.getInstance().getServer().getPlayer(uuid);
        if (other == null) return;

        subscribers.add(subscriber);
        player.getScheduler().run(InvseePlugin.getInstance(), scheduledTask -> player.openInventory(inventory), null);
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
                inventory.setItem(36 + i, armorContents[i]);
            }
        }
        lock.unlock();
    }

    public boolean hasSubscriber(UUID uuid) {
        return subscribers.contains(uuid);
    }

    public void updatePlayerInventory() {
        OfflinePlayer offlinePlayer = InvseePlugin.getInstance().getServer().getOfflinePlayer(uuid);

        if (offlinePlayer instanceof Player player) {
            if (!lock.tryLock()) {
                System.out.println("No lock updatePlayerInv");
                executorService.submit(this::updatePlayerInventory);
            }
            PlayerInventory playerInventory = player.getInventory();

            System.out.println("lock player");
            for (int i = 0; i <= 40; i++) {
                playerInventory.setItem(i, this.inventory.getItem(i));
            }

            lock.unlock();
        }
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
