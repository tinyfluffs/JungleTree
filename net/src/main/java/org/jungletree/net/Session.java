package org.jungletree.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.exception.ChannelClosedException;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.pipeline.CodecHandler;
import org.jungletree.net.protocol.Protocol;
import org.jungletree.net.protocol.Protocols;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Session {
    
    final AtomicBoolean disconnected = new AtomicBoolean();
    final Channel channel;

    @Getter Protocol protocol;

    public Session(Channel channel) {
        this.channel = channel;
        this.protocol = Protocols.HANDSHAKE.getProtocol();
    }

    public void send(Packet pkt) throws ChannelClosedException {
        sendFuture(pkt);
    }

    public void sendAll(Packet... pkts) throws ChannelClosedException {
        for (Packet pkt : pkts) {
            sendFuture(pkt);
        }
    }
    
    public ChannelFuture sendFuture(Packet pkt) throws ChannelClosedException {
        if (!channel.isActive()) {
            throw new ChannelClosedException("Trying to send a message when a session is inactive!");
        }
        return channel.writeAndFlush(pkt).addListener(future -> {
            if (future.cause() != null) {
                onOutboundThrowable(future.cause());
            }
        });
    }
    
    public InetSocketAddress getAddress() {
        var addr = channel.remoteAddress();
        if (!(addr instanceof InetSocketAddress)) {
            return null;
        }
        return (InetSocketAddress) addr;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void packetReceived(T packet) {
        Class<T> clazz = (Class<T>) packet.getClass();
        Handler<T> handler = protocol.getPacketHandler(clazz);
        if (handler == null) {
            return;
        }

        try {
            handler.handle(this, packet);
        } catch (Throwable t) {
            onHandlerThrowable(packet, handler, t);
        }
    }

    public void setProtocol(Protocol protocol) {
        this.channel.flush();
        updatePipeline("codecs", new CodecHandler(protocol));
        this.protocol = protocol;
    }
    
    private void updatePipeline(String key, ChannelHandler handler) {
        this.channel.pipeline().replace(key, key, handler);
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public void disconnect() {
        channel.close();
    }

    public void onConnect() {
    }

    public void onDisconnect() {
        this.disconnected.set(true);
    }

    public void onInboundThrowable(Throwable cause) {
    }
    
    public void onOutboundThrowable(Throwable cause) {
    }

    public <T extends Packet> void onHandlerThrowable(T pkt, Handler<T> handler, Throwable cause) {
        log.error("Error handling {} (handler: {})", pkt, handler.getClass().getSimpleName(), cause);
    }
}
