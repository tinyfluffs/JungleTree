package org.jungletree.net.session;

import org.jungletree.net.Packet;
import org.jungletree.net.exception.ChannelClosedException;
import org.jungletree.net.protocol.Protocol;

public interface Session {
    <T extends Packet> void messageReceived(T message);

    Protocol getProtocol();

    void send(Packet packet) throws ChannelClosedException;

    void sendAll(Packet... packets) throws ChannelClosedException;

    void disconnect();

    void onDisconnect();

    void onReady();

    void onInboundThrowable(Throwable throwable);
}
