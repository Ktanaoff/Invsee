package at.noahb.invsee.common.listener;

import at.noahb.invsee.Constants;
import at.noahb.invsee.InvseePlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LuckPermsListener {

    private final InvseePlugin instance;

    public LuckPermsListener(InvseePlugin instance, LuckPerms luckPerms) {
        this.instance = instance;

        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(this.instance, NodeRemoveEvent.class, this::removeNode);
    }

    private void removeNode(NodeRemoveEvent nodeRemoveEvent) {
        if (!nodeRemoveEvent.isUser()) {
            return;
        }
        OfflinePlayer offlinePlayer = this.instance.getServer().getOfflinePlayer(((User) nodeRemoveEvent.getTarget()).getUniqueId());
        if (!(offlinePlayer instanceof Player player)) {
            return;
        }

        String permission = nodeRemoveEvent.getNode().getKey();

        if (Objects.equals(this.instance.getInvseeCommand().getPermission(), permission)) {
            this.instance.getInvseeSessionManager().removeSubscriberFromSession(player);
        } else if (Objects.equals(Constants.LOOKUP_UNSEEN_PERMISSION, permission)) {
            this.instance.getInvseeSessionManager().getSessionForSubscriber(offlinePlayer.getUniqueId()).ifPresent(session -> {
                if (!Bukkit.getOfflinePlayer(session.getUniqueIdOfObservedPlayer()).isOnline()) {
                    this.instance.getInvseeSessionManager().removeSubscriberFromSession(player);
                }
            });
            this.instance.getEnderseeSessionManager().getSessionForSubscriber(offlinePlayer.getUniqueId()).ifPresent(session -> {
                if (!Bukkit.getOfflinePlayer(session.getUniqueIdOfObservedPlayer()).isOnline()) {
                    this.instance.getEnderseeSessionManager().removeSubscriberFromSession(player);
                }
            });
        } else if (Objects.equals(this.instance.getEnderseeCommand().getPermission(), permission)) {
            this.instance.getEnderseeSessionManager().removeSubscriberFromSession(player);
        }
    }
}
