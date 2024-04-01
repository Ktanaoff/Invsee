package at.noahb.invsee.endersee.command;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.command.AbstractInvseeCommand;
import at.noahb.invsee.common.session.SessionManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderseeCommand extends AbstractInvseeCommand {

    public EnderseeCommand(InvseePlugin instance) {
        super(
                instance,
                "endersee",
                "Endersee command",
                "/endersee <player>",
                "invsee.invsee.command",
                List.of("esee", "es", "ecsee")
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return super.execute(sender, commandLabel, args);
    }

    @Override
    protected SessionManager getSessionManager() {
        return getInstance().getEnderseeSessionManager();
    }

}
