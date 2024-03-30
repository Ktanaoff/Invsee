package at.noahb.endersee.session.manager;

import at.noahb.common.InvseePlugin;
import at.noahb.common.session.manager.SessionManager;
import at.noahb.endersee.session.EnderseeSession;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class EnderseeSessionManager extends SessionManager {
    public EnderseeSessionManager(InvseePlugin instance) {
        super(instance);
    }

    @Override
    protected EnderseeSession createSession(OfflinePlayer offlinePlayer, UUID subscriber) {
        EnderseeSession enderseeSession = new EnderseeSession(offlinePlayer, subscriber);
        addSession(enderseeSession);
        return enderseeSession;
    }

    @Override
    protected EnderseeSession createSession(OfflinePlayer offlinePlayer) {
        return createSession(offlinePlayer, null);
    }
}
