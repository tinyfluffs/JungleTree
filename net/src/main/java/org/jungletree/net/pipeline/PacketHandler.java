package org.jungletree.net.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.NetworkServer;
import org.jungletree.net.Packet;
import org.jungletree.net.Session;

import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    AtomicReference<Session> session = new AtomicReference<>(null);
    NetworkServer networkServer;

    public PacketHandler(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel c = ctx.channel();
        Session s = networkServer.newSession(c);
        if (!this.session.compareAndSet(null, s)) {
            throw new IllegalStateException("Session may not be set more than once");
        }
        s.onConnect();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = this.session.get();
        session.onDisconnect();
        networkServer.sessionInactivated(session);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet i) {
        session.get().packetReceived(i);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        var s = session.get();
        if (s == null) {
            log.error("", cause);
        } else {
            s.onInboundThrowable(cause);
        }
    }

    @Tolerate
    public Session getSession() {
        return session.get();
    }
}
