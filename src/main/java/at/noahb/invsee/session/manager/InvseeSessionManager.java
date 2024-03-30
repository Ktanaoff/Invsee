package at.noahb.invsee.session.manager;

import at.noahb.common.InvseePlugin;
import at.noahb.common.session.manager.SessionManager;
import at.noahb.invsee.session.InvseeSession;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class InvseeSessionManager extends SessionManager {

    public InvseeSessionManager(InvseePlugin instance) {
        super(instance);
    }

    @Override
    public InvseeSession createSession(OfflinePlayer offlinePlayer, UUID subscriber) {
        InvseeSession invseeSession = new InvseeSession(offlinePlayer, subscriber);
        addSession(invseeSession);
        return invseeSession;
    }

    @Override
    public InvseeSession createSession(OfflinePlayer offlinePlayer) {
        return createSession(offlinePlayer, null);
    }
}
