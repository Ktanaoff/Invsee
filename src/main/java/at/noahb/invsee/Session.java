package at.noahb.invsee;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;



public class Session {

    private final UUID uuid;

    private final Set<UUID> subscribers;

    private final Inventory inventory;

    public Session(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.subscribers = new HashSet<>();

        //todo remove this if holder is not needed
        if (player instanceof Player onlinePlayer) {
            this.inventory = Bukkit.createInventory(onlinePlayer, 45);
        } else {
            this.inventory = Invsee.getInstance().getServer().createInventory(null, 45);
        }

        initInventory();
    }

    private void initInventory() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer instanceof Player player) {
            ItemStack[] playerInv = player.getInventory().getContents();
            System.out.println(Arrays.toString(playerInv));
            for (int i = 0; i < playerInv.length; i++) {
                inventory.setItem(i, playerInv[i]);
            }
            ItemStack[] armorContents = player.getInventory().getArmorContents();

            for (int i = 0; i < armorContents.length; i++) {
                inventory.setItem(36+i, armorContents[i]);
            }
        }
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
        player.openInventory(inventory);
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

    public void removeSubscriber(UUID subscriber) {
        subscribers.remove(subscriber);
    }
}
