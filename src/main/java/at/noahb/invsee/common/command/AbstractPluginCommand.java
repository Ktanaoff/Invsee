package at.noahb.invsee.common.command;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.session.SessionManager;
import net.kyori.adventure.text.Component;
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
            sender.sendMessage("Command can only be executed by a player.");
            return true;
        }

        if (!player.hasPermission(Objects.requireNonNull(getPermission(), this::getCommandPermission))) {
            sender.sendMessage("You don't have permissions to use that command");
            return true;
        }

        OfflinePlayer other = instance.getServer().getOfflinePlayer(args[0]);
        getSessionManager().addSubscriberToSession(other, player.getUniqueId());

        return true;
    }

    protected InvseePlugin getInstance() {
        return instance;
    }

    protected abstract String getCommandPermission();

    protected abstract SessionManager getSessionManager();
}
