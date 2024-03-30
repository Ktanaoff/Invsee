package at.noahb.invsee;

import at.noahb.invsee.command.InvseeCommand;
import at.noahb.invsee.listener.InventoryListener;
import at.noahb.invsee.listener.LuckPermsListener;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Invsee extends JavaPlugin {

    private static Invsee instance;
    private SessionManager sessionManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        sessionManager = new SessionManager(instance);
        getServer().getCommandMap().register("invsee", new InvseeCommand(this));
        getServer().getPluginManager().registerEvents(new InventoryListener(instance), this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        new LuckPermsListener(this, luckPerms);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public static Invsee getInstance() {
        return instance;
    }
}
