package at.noahb.invsee.endersee.command;

import at.noahb.invsee.InvseePlugin;
import at.noahb.invsee.common.command.AbstractPluginCommand;
import at.noahb.invsee.common.session.SessionManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderseeCommand extends AbstractPluginCommand {

    private static final String PERMISSION = "invsee.endersee.command";
    public EnderseeCommand(InvseePlugin instance) {
        super(
                instance,
                "endersee",
                "Endersee command",
                "/endersee <player>",
                PERMISSION,
                List.of("esee", "es", "ecsee")
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return super.execute(sender, commandLabel, args);
    }

    @Override
    protected String getCommandPermission() {
        return PERMISSION;
    }

    @Override
    protected SessionManager getSessionManager() {
        return getInstance().getEnderseeSessionManager();
    }

}
