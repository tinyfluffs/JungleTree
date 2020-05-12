package org.jungletree.net.pipeline;

import io.netty.channel.*;
import org.jungletree.net.ConnectionManager;
import org.jungletree.net.Packet;
import org.jungletree.net.session.Session;

import java.util.concurrent.atomic.AtomicReference;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    private final AtomicReference<Session> session = new AtomicReference<>(null);
    private final ConnectionManager connectionManager;

    public PacketHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel c = ctx.channel();
        Session s = connectionManager.newSession(c);
        if (!this.session.compareAndSet(null, s)) {
            throw new IllegalStateException("Session may not be set more than once");
        }
        s.onReady();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = this.session.get();
        session.onDisconnect();
        connectionManager.sessionInactivated(session);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet i) {
        session.get().messageReceived(i);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        session.get().onInboundThrowable(cause);
    }

    public Session getSession() {
        return session.get();
    }
}
