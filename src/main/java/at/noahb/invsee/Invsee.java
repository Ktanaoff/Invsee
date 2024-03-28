package at.noahb.invsee;

import at.noahb.invsee.command.InvseeCommand;
import at.noahb.invsee.listener.InventoryListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Invsee extends JavaPlugin {

    private static Invsee instance;
    private SessionManager sessionManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        sessionManager = new SessionManager(instance);
        getServer().getCommandMap().register("invsee", new InvseeCommand(this));
        getServer().getPluginManager().registerEvents(new InventoryListener(instance), this);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public static Invsee getInstance() {
        return instance;
    }
}
