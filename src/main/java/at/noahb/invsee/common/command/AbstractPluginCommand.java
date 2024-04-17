package at.noahb.invsee.common.command;

import at.noahb.invsee.Constants;
import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.session.SessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPluginCommand extends Command {

    private final InvseePlugin instance;

    public AbstractPluginCommand(InvseePlugin instance, String name, String description, String usage, String permission, List<String> aliases) {
        super(name, description, usage, aliases);
        this.instance = instance;

        setPermission(permission);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text(getUsage()));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed by a player.", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission(Objects.requireNonNull(getPermission(), this::getCommandPermission))) {
            sender.sendMessage(Component.text("You don't have permissions to use that command", NamedTextColor.RED));
            return true;
        }

        OfflinePlayer other = this.instance.getServer().getOfflinePlayer(args[0]);

        if (!other.hasPlayedBefore()) {
            if (!InvseePlugin.getInstance().getConfig().getBoolean(Constants.LOOKUP_UNSEEN_CONFIG)) {
                player.sendMessage(Component.text("Player ", NamedTextColor.RED)
                        .append(Component.text(Objects.requireNonNullElse(other.getName(), other.getUniqueId().toString())))
                        .append(Component.text(" has never played on this server.")));
                return true;
            }

            if (!player.hasPermission(Constants.LOOKUP_UNSEEN_PERMISSION)) {
                player.sendMessage(Component.text("Player ", NamedTextColor.RED)
                        .append(Component.text(Objects.requireNonNullElse(other.getName(), other.getUniqueId().toString())))
                        .append(Component.text(" has never played on this server.")));
                return true;
            }
        }

        getSessionManager().addSubscriberToSession(other, player.getUniqueId());
        return true;
    }

    protected InvseePlugin getInstance() {
        return this.instance;
    }

    protected abstract String getCommandPermission();

    protected abstract SessionManager getSessionManager();
}
