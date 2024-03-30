package at.noahb.invsee.command;

import at.noahb.invsee.Invsee;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InvseeCommand extends Command {

    protected final Invsee instance;

    public InvseeCommand(Invsee instance) {
        super(
                "invsee",
                "Invsee command",
                "/invsee player",
                new ArrayList<>()
        );
        this.instance = instance;
        setPermission("invsee.invsee.command");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("/invsee <player>"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command can only be executed by a player.");
            return true;
        }

        if (!player.hasPermission("invsee.invsee.command")) {
            sender.sendMessage("You don't have permissions to use that command");
        }

        OfflinePlayer other = instance.getServer().getOfflinePlayer(args[0]);

        if (other instanceof Player) {
            instance.getSessionManager().addSubscriberToSession(other, player.getUniqueId());
            return true;
        }

        //todo OfflinePlayer logic

        return true;
    }

}
