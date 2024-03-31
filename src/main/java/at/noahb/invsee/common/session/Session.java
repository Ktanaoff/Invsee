package at.noahb.invsee.common.session;

import at.noahb.invsee.InvseePlugin;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public interface Session {
    UUID getUuid();

    void updatePlayerInventory();

    void updateSpectatorInventory();

    Inventory getInventory();

    Set<UUID> getSubscribers();

    void removeSubscriber(UUID subscriber);

    boolean hasSubscriber(UUID subscriber);

    ReentrantLock getLock();

    void cache(Player player);

    Player getCachedPlayer();

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

    default void save() {
        Player cachedPlayer = getCachedPlayer();
        if (cachedPlayer != null) {
            cachedPlayer.saveData();
        }
    }

    default void update(Runnable runnable) {
        try {
            getLock().lock();
            runnable.run();
            if (isOffline()) {
                save();
            }
        } finally {
            if (getLock().isHeldByCurrentThread()) getLock().unlock();
        }
    }

    default boolean isOffline() {
        return !InvseePlugin.getInstance().getServer().getOfflinePlayer(getUuid()).isOnline();
    }

    default Optional<Player> getPlayerOffline(OfflinePlayer player) {
        Player cached = getCachedPlayer();
        if (cached != null) {
            return Optional.of(cached);
        }
        System.out.println("offline");
        GameProfile profile = new GameProfile(player.getUniqueId(),
                player.getName() != null ? player.getName() : player.getUniqueId().toString());
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel level = server.getLevel(Level.OVERWORLD);

        if (level == null) {
            return Optional.empty();
        }

        ServerPlayer serverPlayer = new ServerPlayer(server, level, profile, ClientInformation.createDefault());

        Player target = serverPlayer.getBukkitEntity();
        target.loadData();
        cache(target);
        return Optional.of(target);
    }
}
