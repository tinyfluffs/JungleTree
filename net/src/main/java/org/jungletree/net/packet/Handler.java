package org.jungletree.net.packet;

import org.jungletree.net.Packet;
import org.jungletree.net.session.Session;

public interface Handler<T extends Packet> {
    void handle(Session session, T pkt);
}
