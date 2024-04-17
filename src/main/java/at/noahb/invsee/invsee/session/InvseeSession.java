package at.noahb.invsee.invsee.session;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.session.Session;
import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;


public class InvseeSession implements Session {

    private final UUID uuid;
    private final Set<UUID> subscribers;
    private final Inventory inventory;
    private final ReentrantLock lock = new ReentrantLock();
    private final Cache<UUID, Player> playerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    public InvseeSession(OfflinePlayer offlinePlayer, UUID subscriber) {
        this.uuid = offlinePlayer.getUniqueId();
        this.subscribers = new HashSet<>();

        if (offlinePlayer instanceof Player player) {
            this.inventory = Bukkit.createInventory(player, 45, player.name().append(text("'s inventory")));
        } else {
            String name = offlinePlayer.getName() == null ? "unknown" : offlinePlayer.getName();
            this.inventory = InvseePlugin.getInstance().getServer().createInventory(null, 45, text(name).append(text("'s inventory")));
        }

        updateSubscriberInventory();
        addSubscriber(subscriber);
    }

    @Override
    public UUID getUniqueIdOfObservedPlayer() {
        return this.uuid;
    }

    @Override
    public Set<UUID> getSubscribers() {
        return this.subscribers;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
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
        this.subscribers.remove(subscriber);
    }

    @Override
    public void updateSubscriberInventory() {
        update(() -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uuid);
            PlayerInventory playerInv = getPlayerInventory(offlinePlayer);

            if (playerInv == null) {
                return;
            }

            for (int i = 0; i < 41; i++) {
                this.inventory.setItem(i, playerInv.getItem(i));
            }
            if (offlinePlayer instanceof Player player) {
                this.inventory.setItem(41, player.getItemOnCursor());
            }
            replaceEmptyPlaceholderSpots();
        });
    }

    private void replaceEmptyPlaceholderSpots() {
        if (this.inventory.getItem(36) == null) this.inventory.setItem(36, Placeholders.BOOTS);
        if (this.inventory.getItem(37) == null) this.inventory.setItem(37, Placeholders.LEGGINGS);
        if (this.inventory.getItem(38) == null) this.inventory.setItem(38, Placeholders.CHESTPLATE);
        if (this.inventory.getItem(39) == null) this.inventory.setItem(39, Placeholders.HELMET);
        if (this.inventory.getItem(40) == null) this.inventory.setItem(40, Placeholders.OFF_HAND);
        if (this.inventory.getItem(41) == null) this.inventory.setItem(41, Placeholders.CURSOR);
    }

    @Override
    public boolean hasSubscriber(UUID uuid) {
        return this.subscribers.contains(uuid);
    }

    @Override
    public void updateObservedInventory() {
        update(() -> {
            OfflinePlayer offlinePlayer = InvseePlugin.getInstance().getServer().getOfflinePlayer(uuid);
            PlayerInventory playerInventory = getPlayerInventory(offlinePlayer);
            if (playerInventory == null) {
                return;
            }
            for (int i = 0; i < playerInventory.getSize(); i++) {
                if (Placeholders.contains(this.inventory.getItem(i))) continue;
                playerInventory.setItem(i, this.inventory.getItem(i));
            }

            if (!Placeholders.contains(this.inventory.getItem(41)) && offlinePlayer instanceof Player player) {
                player.setItemOnCursor(this.inventory.getItem(41));
            }

            replaceEmptyPlaceholderSpots();
        });
    }

    @Override
    public ReentrantLock getLock() {
        return this.lock;
    }

    @Override
    public void cache(Player player) {
        this.playerCache.put(this.uuid, player);
    }

    @Override
    public Player getCachedPlayer() {
        return this.playerCache.getIfPresent(this.uuid);
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

    public enum ArmorSlot {
        HELMET(MaterialTags.HELMETS),
        CHESTPLATE(MaterialTags.CHESTPLATES),
        LEGGINGS(MaterialTags.LEGGINGS),
        BOOTS(MaterialTags.BOOTS);

        private final MaterialSetTag tag;

        ArmorSlot(MaterialSetTag tag) {
            this.tag = tag;
        }

        public boolean checkIfItemFitsSlot(ItemStack itemStack) {
            return this.tag.isTagged(itemStack);
        }
    }

    public static class Placeholders {
        static final ItemStack HELMET = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        static final ItemStack CHESTPLATE = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        static final ItemStack LEGGINGS = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        static final ItemStack BOOTS = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        static final ItemStack OFF_HAND = new ItemStack(Material.BARRIER);
        static final ItemStack CURSOR = new ItemStack(Material.BARRIER);
        static final List<ItemStack> placeholders = List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS, OFF_HAND, CURSOR);

        static {
            List<Component> lore = List.of(text("empty", RED).decoration(ITALIC, false));
            HELMET.editMeta(itemMeta -> {
                itemMeta.displayName(text("Helmet slot", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });
            CHESTPLATE.editMeta(itemMeta -> {
                itemMeta.displayName(text("Chestplate slot", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });
            LEGGINGS.editMeta(itemMeta -> {
                itemMeta.displayName(text("Leggings slot", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });
            BOOTS.editMeta(itemMeta -> {
                itemMeta.displayName(text("Boots slot", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });
            OFF_HAND.editMeta(itemMeta -> {
                itemMeta.displayName(text("Off Hand", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });
            CURSOR.editMeta(itemMeta -> {
                itemMeta.displayName(text("Cursor", GOLD).decoration(ITALIC, false));
                itemMeta.lore(lore);
            });

        }

        public static boolean isOffHandPlaceholder(ItemStack itemStack) {
            return OFF_HAND.equals(itemStack);
        }

        public static boolean contains(ItemStack itemStack) {
            return placeholders.contains(Objects.requireNonNullElse(itemStack, ItemStack.empty()));
        }

        public static boolean isCursorPlaceholder(ItemStack itemStack) {
            return CURSOR.equals(itemStack);
        }
    }

}