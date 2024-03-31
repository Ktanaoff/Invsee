package at.noahb.invsee.endersee.command;

import at.noahb.invsee.InvseePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnderseeCommand extends Command {

    protected final InvseePlugin instance;

    public EnderseeCommand(InvseePlugin instance) {
        super(
                "endersee",
                "Endersee command",
                "/endersee player",
                new ArrayList<>()
        );
        this.instance = instance;
        setAliases(List.of("esee", "es", "ecsee"));
        setPermission("invsee.invsee.command");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("/endersee <player>"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command can only be executed by a player.");
            return true;
        }

        if (!player.hasPermission("invsee.endersee.command")) {
            sender.sendMessage("You don't have permissions to use that command");
            return true;
        }

        OfflinePlayer other = instance.getServer().getOfflinePlayer(args[0]);
        instance.getEnderseeSessionManager().addSubscriberToSession(other, player.getUniqueId());

        return true;
    }

}
