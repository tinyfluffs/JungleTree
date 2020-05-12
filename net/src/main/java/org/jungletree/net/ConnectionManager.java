package org.jungletree.net;

import io.netty.channel.Channel;
import org.jungletree.net.session.Session;

public interface ConnectionManager {
    Session newSession(Channel c);

    void sessionInactivated(Session session);

    void shutdown();
}
