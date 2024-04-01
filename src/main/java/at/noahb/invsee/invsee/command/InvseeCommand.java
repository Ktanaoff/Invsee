package at.noahb.invsee.invsee.command;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.command.AbstractInvseeCommand;
import at.noahb.invsee.common.session.SessionManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InvseeCommand extends AbstractInvseeCommand {


    public InvseeCommand(InvseePlugin instance) {
        super(
                instance,
                "invsee",
                "Invsee command",
                "/invsee <player>",
                "invsee.invsee.command",
                List.of("isee", "is", "inv")
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return super.execute(sender, commandLabel, args);
    }

    @Override
    protected SessionManager getSessionManager() {
        return getInstance().getInvseeSessionManager();
    }

}
