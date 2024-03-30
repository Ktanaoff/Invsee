package at.noahb.invsee;

import at.noahb.invsee.common.listener.InventoryListener;
import at.noahb.invsee.common.listener.LuckPermsListener;
import at.noahb.invsee.endersee.command.EnderseeCommand;
import at.noahb.invsee.endersee.session.EnderseeSessionManager;
import at.noahb.invsee.invsee.command.InvseeCommand;
import at.noahb.invsee.invsee.session.InvseeSessionManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class InvseePlugin extends JavaPlugin {

    private static InvseePlugin instance;
    private InvseeSessionManager invseeSessionManager;
    private EnderseeSessionManager enderseeSessionManager;
    private LuckPerms luckPerms;
    private Command enderseeCommand;
    private Command invseeCommand;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        invseeSessionManager = new InvseeSessionManager(instance);
        enderseeSessionManager = new EnderseeSessionManager(instance);
        registerCommands();
        getServer().getPluginManager().registerEvents(new InventoryListener(instance), this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        new LuckPermsListener(this, luckPerms);
    }

    private void registerCommands() {
        invseeCommand = new InvseeCommand(this);
        getServer().getCommandMap().register("invsee", invseeCommand);
        enderseeCommand = new EnderseeCommand(this);
        getServer().getCommandMap().register("endersee", enderseeCommand);
    }

    public InvseeSessionManager getInvseeSessionManager() {
        return invseeSessionManager;
    }

    public EnderseeSessionManager getEnderseeSessionManager() {
        return enderseeSessionManager;
    }

    public Command getEnderseeCommand() {
        return enderseeCommand;
    }

    public Command getInvseeCommand() {
        return invseeCommand;
    }

    public static InvseePlugin getInstance() {
        return instance;
    }
}
